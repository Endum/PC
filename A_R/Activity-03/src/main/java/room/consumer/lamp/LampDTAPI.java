package room.consumer.lamp;
import io.vertx.core.Future;
import room.consumer.DTAPI;

/**
 * Toy Lamp Digital Twin API 
 *   
 * @author aricci
 *
 */
public interface LampDTAPI extends DTAPI{
	/**
	 * 
	 * Turn on the lamp.
	 * 
	 */
	Future<Void> turnOn();

	/**
	 * 
	 * Turn off the lamp.
	 * 
	 */
	Future<Void> turnOff();
}
