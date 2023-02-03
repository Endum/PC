package smart_room.distributed;

import smart_room.Event;

public class TestLumSensorDevice {

	public static void main(String[] args) throws Exception {

		LuminositySensorSimulator ls = new LuminositySensorSimulator("MyLightSensor");
		ls.init();
		
		ls.register((Event ev) -> {
			System.out.println("New event: " + ev);
		});
	
		while (true) {
			System.out.println(ls.getLuminosity());
			Thread.sleep(1000);
		}
	}

}
