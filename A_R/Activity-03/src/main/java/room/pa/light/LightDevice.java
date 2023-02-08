package room.pa.light;

import common.EventSource;

public interface LightDevice extends EventSource {
	
	double getLuminosity();

}
