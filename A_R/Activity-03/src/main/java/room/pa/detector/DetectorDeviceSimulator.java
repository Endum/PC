/**
 * Simulator/mock for a detector device
 * 
 */
package room.pa.detector;

import common.AbstractEventSource;

public class DetectorDeviceSimulator extends AbstractEventSource implements DetectorDevice {

	private boolean isPresenceDetected;
	private DetectorSimFrame frame;
	private String detectorID;
	
	public DetectorDeviceSimulator(String detectorID){
		this.detectorID = detectorID;
	}
	
	public void init() {
		frame = new DetectorSimFrame(this, detectorID);
		frame.display();
	}

	@Override
	public synchronized boolean presenceDetected() {
		return isPresenceDetected;
	}

	synchronized void updateValue(boolean value) {
		long ts = System.currentTimeMillis();
		this.isPresenceDetected = value;
		if (value) {
			this.notifyEvent(new PresenceDetected(ts));
		} else {
			this.notifyEvent(new PresenceNoMoreDetected(ts));
		}
	}
}
