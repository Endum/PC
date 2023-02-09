package room.consumer.detector;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

public class VanillaDetectorDTConsumerAgent extends AbstractVerticle {

	
	private DetectorDTAPI lampDT;
	private Handler<JsonObject> eventHandler;
	
	public VanillaDetectorDTConsumerAgent(DetectorDTAPI thing, Handler<JsonObject> eventHandler) {
		this.lampDT = thing; 
		this.eventHandler = eventHandler;
	}
	
	/**
	 * Main agent body.
	 */
	public void start(Promise<Void> startPromise) throws Exception {
		log("Detector consumer agent started.");		
		
		log("Getting the status...");		

		lampDT.getState()
			.onSuccess(res -> {
				log("State: " + res);			
			})
			.onFailure(err -> {
				log("Failure " + err);
			});
	
		lampDT
			.subscribe(this.eventHandler)
			.onComplete(res3 -> {
				log("Subscribed!");
			});		
	}
	
	protected void log(String msg) {
		System.out.println("[DetectorDTConsumerAgent]["+System.currentTimeMillis()+"] " + msg);
	}
	
}
