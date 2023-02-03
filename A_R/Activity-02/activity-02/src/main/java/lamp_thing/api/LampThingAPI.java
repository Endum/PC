package lamp_thing.api;

import io.vertx.core.Future;

/**
 * Toy LightThing API 
 *   
 * @author aricci
 *
 */
public interface LampThingAPI extends ThingAPI{
	
	/* properties */

	/**
	 * 
	 * Get the state of the light.
	 * 
	 */
	Future<String> getState();
	
	/* actions */

	/**
	 * 
	 * Turn on the light.
	 * 
	 * @return
	 */
	Future<Void> on();

	/**
	 * 
	 * Turn off the light.
	 * 
	 * @return
	 */
	Future<Void> off();

}
