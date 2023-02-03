package lamp_thing.consumers;

import java.io.Console;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import lamp_thing.api.LampThingAPI;

public class LampThingConsumerAgent extends AbstractVerticle {

	
	private LampThingAPI thing;
	private int nEventsReceived;

	private double luminosityLevel = 0.0;
	private boolean detected = false;
	
	public LampThingConsumerAgent(LampThingAPI thing) {
		this.thing = thing; 
		nEventsReceived = 0;
	}
	
	/**
	 * Main agent body.
	 */
	public void start(Promise<Void> startPromise) throws Exception {
		log("Lamp consumer agent started.");	
		log("Subscribing...");
		thing.subscribe(this::onNewEvent).onComplete(res -> {
			log("Subscribed");
		});		
	}
	
	
	/**
	 * Handler to process observed events  
	 */
	protected void onNewEvent(JsonObject ev) {
		nEventsReceived++;
		log("New event: \n " + ev.toString() + "\nNum events received: " + nEventsReceived);

		if(ev.getJsonObject("data").containsKey("luminosity")) {
			this.luminosityLevel = Double.parseDouble(ev.getJsonObject("data").getString("luminosity"));
		} else if(ev.getJsonObject("data").containsKey("detection")){
			this.detected = Boolean.parseBoolean(ev.getJsonObject("data").getString("detection"));
		}

		thing.getState().compose(res -> {
			log("LAMP: " + res);
			log("DETE: " + this.detected);
			log("LUMI: " + this.luminosityLevel);
			if(detected) {
				if(luminosityLevel < 0.2) {
					if(res.equals("off")) {
						log("Turning light on.");
						return thing.on();
					} else {
						return Future.succeededFuture();
					}
				} else {
					if(res.equals("on")) {
						log("Turning light off.");
						return thing.off();
					} else {
						return Future.succeededFuture();
					}
				}
			} else {
				if(res.equals("on")) {
					log("Turning light off.");
					return thing.off();
				} else {
					return Future.succeededFuture();
				}
			}
		}).onFailure(err -> {
			log("Failure " + err);
		});
	}
	
	protected void log(String msg) {
		System.out.println("[LampThingConsumerAgent]["+System.currentTimeMillis()+"] " + msg);
	}
	
}