package lamp_thing.api;

import io.vertx.core.Future;

public interface DetectionThingAPI extends ThingAPI{
    
    /* Properties */

    /**
     * 
     * Get the state of detection sensor.
     * 
     */
    Future<String> getDetection();

}
