package room.dt.lamp;

import io.vertx.core.Future;

/**
 * Lamp Digital Twin Java API for the Shadowing Layer
 *
 */
public interface LampDTShadAPI {
	
	
	Future<String> updateState(String newState, long timeStamp);
	

}
