package room.consumer;

import io.vertx.core.Vertx;
import room.consumer.detector.*;
import room.consumer.lamp.*;
import room.consumer.light.*;;

public class RunDTConsumer {

    static final int LAMP_DT_PORT = 8888;
    static final int DETECTOR_DT_PORT = 8889;
    static final int LIGHT_DT_PORT = 8890;

	static double light_level = 0.0;
	static boolean presence_detected = false;

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
	
		LampDTProxy lampThing = new LampDTProxy("localhost", LAMP_DT_PORT);
		lampThing.setup(vertx).onSuccess(h -> {
			vertx.deployVerticle(new VanillaLampDTConsumerAgent(lampThing, ev -> {
				System.out.println("Lamp: " + ev.encodePrettily());
            }));
		});

        DetectorDTProxy detectorThing = new DetectorDTProxy("localhost", DETECTOR_DT_PORT);
		detectorThing.setup(vertx).onSuccess(h -> {
			vertx.deployVerticle(new VanillaDetectorDTConsumerAgent(detectorThing, ev -> {
				System.out.println("Detector: " + ev.encodePrettily());

				presence_detected = ev.getBoolean("state");
				checkForToggle(lampThing);
            }));
		});

        LightDTProxy lightThing = new LightDTProxy("localhost", LIGHT_DT_PORT);
		lightThing.setup(vertx).onSuccess(h -> {
			vertx.deployVerticle(new VanillaLightDTConsumerAgent(lightThing, ev -> {
				System.out.println("Light: " + ev.encodePrettily());

				light_level = ev.getDouble("state");
				checkForToggle(lampThing);
            }));
		});
	}

	public static void checkForToggle(LampDTProxy lamp) {
		if(presence_detected) {
			if(light_level < 0.2) {
				lamp.turnOn();
			} else {
				lamp.turnOff();
			}
		} else {
			lamp.turnOff();
		}
	}
}
