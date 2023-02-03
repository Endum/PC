package lamp_thing.consumers;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import lamp_thing.api.LuminosityThingAPI;

public class LuminosityThingHTTPProxy extends ThingHTTPProxy implements LuminosityThingAPI{

    private static final String PROPERTY_LUMINOSITY = "/api/properties/luminosity";
			
	public LuminosityThingHTTPProxy(String thingHost, int thingPort) {
		super("LuminosityThingHTTPProxy", thingHost, thingPort);
	}

    @Override
    public Future<String> getLuminosity() {
        Promise<String> promise = Promise.promise();
		client
			.get(this.thingPort, thingHost, PROPERTY_LUMINOSITY)
			.send()
			.onSuccess(response -> {
				JsonObject reply = response.bodyAsJsonObject();
				String luminosity = reply.getString("luminosity");
				promise.complete(luminosity);
			})
			.onFailure(err -> {
				promise.fail("Something went wrong " + err.getMessage());
			});
		return promise.future();
    }
    
}
