package lamp_thing.api;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

public interface ThingAPI {

    /**
	 * Get the TD.
	 * 
	 * @return
	 */
	Future<JsonObject> getTD();

    /**
	 * Subscribe to events generated by the thing device.
	 * 
	 * @param handler
	 * @return
	 */
	Future<Void> subscribe(Handler<JsonObject> handler);
    
}
