package room.consumer;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.JsonObject;
import room.consumer.detector.*;
import room.consumer.lamp.*;
import room.consumer.light.*;;

public class SmartRoom implements LampDTAPI, DetectorDTAPI, LightDTAPI{

    static final int LAMP_DT_PORT = 8888;
    static final int DETECTOR_DT_PORT = 8889;
    static final int LIGHT_DT_PORT = 8890;

	private double light_level = 0.0;
	private boolean presence_detected = false;

	LampDTProxy lampThing;
	DetectorDTProxy detectorThing;
	LightDTProxy lightThing;

	Vertx vertx;

	public SmartRoom(String lampHost, String detectorHost, String lightHost) {
		this.vertx = Vertx.vertx();
	
		this.lampThing = new LampDTProxy(lampHost, LAMP_DT_PORT);
		lampThing.setup(vertx).onSuccess(h -> {
			vertx.deployVerticle(new VanillaLampDTConsumerAgent(lampThing, ev -> {
				System.out.println("Lamp: " + ev.encodePrettily());
            }));
		});

        this.detectorThing = new DetectorDTProxy(detectorHost, DETECTOR_DT_PORT);
		detectorThing.setup(vertx).onSuccess(h -> {
			vertx.deployVerticle(new VanillaDetectorDTConsumerAgent(detectorThing, ev -> {
				System.out.println("Detector: " + ev.encodePrettily());

				this.presence_detected = ev.getBoolean("state");
				this.checkForToggle();
            }));
		});

        this.lightThing = new LightDTProxy(lightHost, LIGHT_DT_PORT);
		lightThing.setup(vertx).onSuccess(h -> {
			vertx.deployVerticle(new VanillaLightDTConsumerAgent(lightThing, ev -> {
				System.out.println("Light: " + ev.encodePrettily());

				this.light_level = ev.getDouble("state");
				this.checkForToggle();
            }));
		});
	}

	private void checkForToggle() {
		if(presence_detected) {
			if(light_level < 0.2) {
				lampThing.turnOn();
			} else {
				lampThing.turnOff();
			}
		} else {
			lampThing.turnOff();
		}
	}

	@Override
	public Future<JsonObject> getState() {
		Promise<JsonObject> promise = Promise.promise();
		CompositeFuture.all(this.lampThing.getState(), this.detectorThing.getState(), this.lightThing.getState())
			.onComplete(cf -> {
				promise.complete(JsonObject.mapFrom(cf.result().list()));
			});
		return promise.future();
	}

	@Override
	public Future<Void> subscribe(Handler<JsonObject> handler) {
		Promise<Void> promise = Promise.promise();
		CompositeFuture.all(
			this.lampThing.subscribe(handler),
			this.detectorThing.subscribe(handler),
			this.lightThing.subscribe(handler)
		).onComplete(cf -> {
			promise.complete();
		});
		return promise.future();
	}

	@Override
	public Future<Void> turnOn() {
		return lampThing.turnOn();
	}

	@Override
	public Future<Void> turnOff() {
		return lampThing.turnOff();
	}

}
