package lamp_thing.impl;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lamp_thing.api.LuminosityThingAPI;
import smart_room.LightLevelChanged;

/**
 * 
 * Behaviour of the Lamp Thing 
 * 
 * @author aricci
 *
 */
public class LuminosityThingModel implements LuminosityThingAPI {

	private Vertx vertx;

	private String luminosity;
	
	private String thingId;
	private JsonObject td;
	private LuminositySensorSimulator ld;
	

	public LuminosityThingModel(String thingId) {
		log("Creating the light thing simulator.");
		this.thingId = thingId;
		
	    luminosity = "0";
	    
		ld = new LuminositySensorSimulator(thingId);
		ld.init();	    
	}
	
	public void setup(Vertx vertx) {
		this.vertx = vertx;
		td = new JsonObject();
		
		td.put("@context", "https://www.w3.org/2019/wot/td/v1");
		td.put("id", thingId);
		td.put("title", "MyLuminosityThing");
		
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
		JsonObject luminosity = new JsonObject();
		props.put("luminosity", luminosity);		
		luminosity.put("type", "string");
		luminosity.put("forms", new JsonArray());
		
		/* events */
		
		JsonObject events = new JsonObject();
		td.put("events", events);
		JsonObject stateChanged = new JsonObject();
		events.put("luminosityChanged", stateChanged);		
		JsonObject data = new JsonObject();
		stateChanged.put("data", data);
		JsonObject dataType = new JsonObject();
		data.put("type", dataType);
		dataType.put("state", "string");
		dataType.put("timestamp", "decimal"); // better would be: "time"
		stateChanged.put("forms",  new JsonArray());

		ld.register(ev -> {
			this.luminosity = String.valueOf(((LightLevelChanged)ev).getNewLevel());
			notifyNewPropertyStatus();
		});

	}

	public Future<JsonObject> getTD() {
		Promise<JsonObject> p = Promise.promise();
		p.complete(td);
		return p.future();
	}

	@Override
	public Future<String> getLuminosity() {
		Promise<String> p = Promise.promise();
		synchronized (this) {
			p.complete(luminosity);
		}
		return p.future();
	}
	
	private void notifyNewPropertyStatus() {
	    JsonObject ev = new JsonObject();
		ev.put("event", "luminosityChanged");
	    JsonObject data = new JsonObject();
		data.put("luminosity", luminosity);
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
		System.out.println("[LuminosityThingModel]["+System.currentTimeMillis()+"] " + msg);
	}

}
