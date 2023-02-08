package room.pa.detector;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import common.ThingAbstractAdapter;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;

/**
 * 
 * Presence Detection Thing Service 
 *  
 * @author aricci
 *
 */
public class DetectorThingService extends AbstractVerticle {

	private DetectorThingAPI model;
	private List<ThingAbstractAdapter> adapters;
	
	public static final int HTTP_PORT = 7889;
	public static final int MQTT_PORT = 1889;
	
	public DetectorThingService(DetectorThingAPI model) {
		this.model = model;
		adapters = new LinkedList<ThingAbstractAdapter>();
	}
	
	@Override
	public void start(Promise<Void> startPromise) throws Exception {
		installAdapters(startPromise);
	}	
	
	/**
	 * Installing all available adapters.
	 * 
	 * Typically driven by using some config file.
	 *  	 
	 */
	protected void installAdapters(Promise<Void> promise) {		
	
		ArrayList<Future> allFutures = new ArrayList<Future>();		
		try {
			/*
			 * Installing only the HTTP adapter.
			 */
			DetectorThingHTTPAdapter httpAdapter = new DetectorThingHTTPAdapter(model, "localhost", HTTP_PORT, this.getVertx());
			Promise<Void> p = Promise.promise();
			httpAdapter.setupAdapter(p);
			Future<Void> fut = p.future();
			allFutures.add(fut);
			fut.onSuccess(res -> {
				log("HTTP adapter installed.");
				adapters.add(httpAdapter);
			}).onFailure(f -> {
				log("HTTP adapter not installed.");
			});
		} catch (Exception ex) {
			log("HTTP adapter installation failed.");
		}
					
		try {
			/*
			 * Installing MQTT shadowing.
			 */
			DetectorPAShadMQTT shad = new DetectorPAShadMQTT(model, "MyDetectorDT","localhost", MQTT_PORT, this.getVertx());
			Promise<Void> p = Promise.promise();
			shad.setupAdapter(p);
			Future<Void> fut = p.future();
			allFutures.add(fut);
			fut.onSuccess(res -> {
				log("MQTT shadowing inbound installed.");
			}).onFailure(f -> {
				log("MQTT shadowing inbound not installed.");
			});
		} catch (Exception ex) {
			log("MQTT adapter installation failed.");
		}

		CompositeFuture.all(allFutures).onComplete(res -> {
			log("Adapters installed.");
			promise.complete();
		});
	}

	protected void log(String msg) {
		System.out.println("[DetectorThingService] " + msg);
	}
}
