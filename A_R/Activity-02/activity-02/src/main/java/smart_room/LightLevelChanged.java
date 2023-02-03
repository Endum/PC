package smart_room;

public class LightLevelChanged extends Event {

	private double newLevel;
	
	public LightLevelChanged(long timestamp, double newLevel) {
		super(timestamp);
		this.newLevel = newLevel;
	}
	
	public double getNewLevel() {
		return this.newLevel;
	}
	
}
