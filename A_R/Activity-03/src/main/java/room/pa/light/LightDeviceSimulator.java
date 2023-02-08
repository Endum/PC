/**
 * Simulator/mock for a light sensor
 * 
 */
package room.pa.light;

import common.*;

public class LightDeviceSimulator  extends AbstractEventSource implements LightDevice {

	private double currentLuminosityLevel;
	private String Id;
	private LightSimFrame frame;
	
	public LightDeviceSimulator(String Id){
		this.Id = Id;
	}
	
	public void init() {
		frame = new LightSimFrame(this,Id);
		frame.display();
	}
	
	@Override
	public synchronized double getLuminosity() {
		return currentLuminosityLevel;
	}

    synchronized void updateValue(double value) {
    	long ts = System.currentTimeMillis();
		this.currentLuminosityLevel = value;
		this.notifyEvent(new LightLevelChanged(ts, value));
	}

}
