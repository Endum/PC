package room.dt.detector;

import io.vertx.core.Future;

/**
 * Detector Digital Twin Java API for the Shadowing Layer
 *
 */
public interface DetectorDTShadAPI {
	
	
	Future<String> updateState(String newState, long timeStamp);
	

}
