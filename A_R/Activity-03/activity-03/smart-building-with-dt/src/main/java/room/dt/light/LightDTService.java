package room.dt.light;

import java.util.ArrayList;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;

/**
 * 
 * Light Digital Twin Service 
 *
 */
public class LightDTService extends AbstractVerticle {

	private LightDTModel model;	
	public static final int HTTP_PORT = 8889;
	public static final int MQTT_PORT = 1889;
	
	public LightDTService(LightDTModel model) {
		this.model = model;
	}
	
	@Override
	public void start(Promise<Void> startPromise) throws Exception {
	

		ArrayList<Future> allFutures = new ArrayList<Future>();		
		try {
			LightDTAdapter httpAdapter = new LightDTAdapter(model, HTTP_PORT, this.getVertx());
			Promise<Void> p = Promise.promise();
			httpAdapter.setupAdapter(p);
			Future<Void> fut = p.future();
			allFutures.add(fut);
			fut.onSuccess(res -> {
				log("HTTP adapter installed.");
			}).onFailure(f -> {
				log("HTTP adapter not installed.");
			});
		} catch (Exception ex) {
			log("HTTP adapter installation failed.");
		}

		try {
			LightDTShadMQTT mqttShad = new LightDTShadMQTT(model, "MyLightDT", "MyLight", "localhost", MQTT_PORT);
			Future<Void> fut = mqttShad.setup(this.getVertx());
			allFutures.add(fut);
			fut.onSuccess(res -> {
				log("MQTT shad inbound installed.");
			}).onFailure(f -> {
				log("MQTT shad inbkound not installed.");
			});
		} catch (Exception ex) {
			log("MQTT adapter installation failed.");
		}				
		
		CompositeFuture
			.all(allFutures)
			.onSuccess(res -> {
				log("setup ok.");
				startPromise.complete();
			});
	}

	protected void log(String msg) {
		System.out.println("[LightDTService] " + msg);
	}
}
