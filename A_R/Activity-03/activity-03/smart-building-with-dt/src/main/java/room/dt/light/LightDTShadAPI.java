package room.dt.light;

import io.vertx.core.Future;

/**
 * Light Digital Twin Java API for the Shadowing Layer
 *
 */
public interface LightDTShadAPI {
	
	
	Future<String> updateState(String newState, long timeStamp);
	

}
