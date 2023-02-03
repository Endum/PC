package lamp_thing.impl;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lamp_thing.api.DetectionThingAPI;
import smart_room.PresenceDetected;

/**
 * 
 * Behaviour of the Lamp Thing 
 * 
 * @author aricci
 *
 */
public class PresenceThingModel implements DetectionThingAPI {

	private Vertx vertx;

	private String detection;
	
	private String thingId;
	private JsonObject td;
	private PresDetectSensorSimulator ld;
	

	public PresenceThingModel(String thingId) {
		log("Creating the presence thing simulator.");
		this.thingId = thingId;
		
	    detection = "false";
	    
		ld = new PresDetectSensorSimulator(thingId);
		ld.init();	    
	}
	
	public void setup(Vertx vertx) {
		this.vertx = vertx;
		td = new JsonObject();
		
		td.put("@context", "https://www.w3.org/2019/wot/td/v1");
		td.put("id", thingId);
		td.put("title", "MyPresenceThing");
		
		/* security section */

		JsonArray schemas = new JsonArray();
		td.put("security", schemas );
		JsonObject noSec = new JsonObject();
		noSec.put("scheme", "nosec");
		schemas.add(noSec);
		
		/* affordances */
		
		/* properties */
		
		JsonObject props = new JsonObject();
		td.put("properties", props);
		JsonObject detection = new JsonObject();
		props.put("detection", detection);		
		detection.put("type", "string");
		detection.put("forms", new JsonArray());
		
		/* events */
		
		JsonObject events = new JsonObject();
		td.put("events", events);
		JsonObject stateChanged = new JsonObject();
		events.put("detectionChanged", stateChanged);		
		JsonObject data = new JsonObject();
		stateChanged.put("data", data);
		JsonObject dataType = new JsonObject();
		data.put("type", dataType);
		dataType.put("state", "string");
		dataType.put("timestamp", "decimal"); // better would be: "time"
		stateChanged.put("forms",  new JsonArray());

		ld.register(ev -> {
			this.detection = String.valueOf(ev instanceof PresenceDetected);
			notifyNewPropertyStatus();
		});

	}

	public Future<JsonObject> getTD() {
		Promise<JsonObject> p = Promise.promise();
		p.complete(td);
		return p.future();
	}

	@Override
	public Future<String> getDetection() {
		Promise<String> p = Promise.promise();
		synchronized (this) {
			p.complete(detection);
		}
		return p.future();
	}
	
	private void notifyNewPropertyStatus() {
	    JsonObject ev = new JsonObject();
		ev.put("event", "detectionChanged");
	    JsonObject data = new JsonObject();
		data.put("detection", detection);
		ev.put("data", data);			
		ev.put("timestamp", System.currentTimeMillis());
		this.generateEvent(ev);
	}

	private void generateEvent(JsonObject ev) {
		vertx.eventBus().publish("events", ev);	
	}
	
	public Future<Void> subscribe(Handler<JsonObject> h) {
		Promise<Void> p = Promise.promise();
		vertx.eventBus().consumer("events", ev -> {
			h.handle((JsonObject) ev.body());
		});	
		p.complete();
		return p.future();
	}
	
    protected void log(String msg) {
		System.out.println("[PresenceThingModel]["+System.currentTimeMillis()+"] " + msg);
	}

}
