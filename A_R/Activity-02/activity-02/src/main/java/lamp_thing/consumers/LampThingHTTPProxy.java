package lamp_thing.consumers;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import lamp_thing.api.LampThingAPI;

/**
 * Proxy to interact with a LampThing using HTTP protocol
 * 
 * @author aricci
 *
 */
public class LampThingHTTPProxy extends ThingHTTPProxy implements LampThingAPI{

	private static final String PROPERTY_STATE = "/api/properties/state";
	private static final String ACTION_ON = "/api/actions/on";
	private static final String ACTION_OFF = "/api/actions/off";
			
	public LampThingHTTPProxy(String thingHost, int thingPort) {
		super("LampThingHTTPProxy", thingHost, thingPort);
	}
	
	public Future<String> getState() {
		Promise<String> promise = Promise.promise();
		client
			.get(this.thingPort, thingHost, PROPERTY_STATE)
			.send()
			.onSuccess(response -> {
				JsonObject reply = response.bodyAsJsonObject();
				String status = reply.getString("state");
				promise.complete(status);
			})
			.onFailure(err -> {
				promise.fail("Something went wrong " + err.getMessage());
			});
		return promise.future();
	}
	
	public Future<Void> on() {
		Promise<Void> promise = Promise.promise();
		client
			.post(this.thingPort, thingHost, ACTION_ON)
			.send()
			.onSuccess(response -> {
				promise.complete(null);
			})
			.onFailure(err -> {
				promise.fail("Something went wrong " + err.getMessage());
			});
		return promise.future();
	}
	
	public Future<Void> off() {
		Promise<Void> promise = Promise.promise();
		client
			.post(this.thingPort, thingHost, ACTION_OFF)
			.send()
			.onSuccess(response -> {
				promise.complete(null);
			})
			.onFailure(err -> {
				promise.fail("Something went wrong " + err.getMessage());
			});
		return promise.future();
	}

}
