package smart_room.distributed;

public class TestLightDevice {

	public static void main(String[] args) throws Exception {

		LightDeviceSimulator ld = new LightDeviceSimulator("MyLight");
		ld.init();

		while (true) {
			ld.on();
			Thread.sleep(1000);
			ld.off();
			Thread.sleep(1000);
		}
		
	}

}
