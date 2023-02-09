package room.pa;

import io.vertx.core.Vertx;
import room.pa.detector.DetectorThingModel;
import room.pa.detector.DetectorThingService;
import room.pa.lamp.LampThingModel;
import room.pa.lamp.LampThingService;
import room.pa.light.LightThingModel;
import room.pa.light.LightThingService;

/**
 * Launching the Lamp Thing service.
 * 
 * @author aricci
 *
 */
public class RunThingServices {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();

		LampThingModel lampModel = new LampThingModel("MyLamp");
		lampModel.setup(vertx);
		LampThingService lampService = new LampThingService(lampModel);
		vertx.deployVerticle(lampService);

		DetectorThingModel detectorModel = new DetectorThingModel("MyDetector");
		detectorModel.setup(vertx);
		DetectorThingService detectorService = new DetectorThingService(detectorModel);
		vertx.deployVerticle(detectorService);

		LightThingModel lightModel = new LightThingModel("MyLight");
		lightModel.setup(vertx);
		LightThingService lightService = new LightThingService(lightModel);
		vertx.deployVerticle(lightService);

	}

}
