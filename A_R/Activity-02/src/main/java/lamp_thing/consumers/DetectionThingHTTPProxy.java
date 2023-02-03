package lamp_thing.consumers;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import lamp_thing.api.DetectionThingAPI;

public class DetectionThingHTTPProxy extends ThingHTTPProxy implements DetectionThingAPI{

	private static final String PROPERTY_DETECTION = "/api/properties/detection";
			
	public DetectionThingHTTPProxy(String thingHost, int thingPort) {
		super("DetecgtionThingHTTPProxy", thingHost, thingPort);
	}

    @Override
    public Future<String> getDetection() {
        Promise<String> promise = Promise.promise();
		client
			.get(this.thingPort, thingHost, PROPERTY_DETECTION)
			.send()
			.onSuccess(response -> {
				JsonObject reply = response.bodyAsJsonObject();
				String detection = reply.getString("detection");
				promise.complete(detection);
			})
			.onFailure(err -> {
				promise.fail("Something went wrong " + err.getMessage());
			});
		return promise.future();
    }
    
}
