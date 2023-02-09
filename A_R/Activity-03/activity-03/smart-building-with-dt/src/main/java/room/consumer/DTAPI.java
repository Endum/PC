package room.consumer;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

public interface DTAPI {

    /**
	 * 
	 * Get the state of the DT.
	 * 
	 */
	Future<JsonObject> getState();
	

	/**
	 * 
     * Subscribe to events generated by the digital twin.
	 * 
	 */
	Future<Void> subscribe(Handler<JsonObject> handler);
}