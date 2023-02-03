package lamp_thing.api;

import io.vertx.core.Future;

public interface LuminosityThingAPI extends ThingAPI{
    /* Properties */

    /**
     * 
     * Get the current level of luminosity.
     * 
     */
    Future<String> getLuminosity();
}
