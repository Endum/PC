package room.dt;

import io.vertx.core.Vertx;
import room.dt.lamp.LampDTModel;
import room.dt.lamp.LampDTService;
import room.dt.detector.DetectorDTModel;
import room.dt.detector.DetectorDTService;
import room.dt.light.LightDTModel;
import room.dt.light.LightDTService;

/**
 * Launching Digital Twin services.
 *
 */
public class RunDTServices {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();

		LampDTModel lampModel = new LampDTModel("MyLampDT");
		lampModel.setup(vertx);
		LampDTService lampService = new LampDTService(lampModel);
		vertx.deployVerticle(lampService);

		DetectorDTModel detectorModel = new DetectorDTModel("MyDetectorDT");
		detectorModel.setup(vertx);
		DetectorDTService detectorService = new DetectorDTService(detectorModel);
		vertx.deployVerticle(detectorService);

		LightDTModel lightModel = new LightDTModel("MyLightDT");
		lightModel.setup(vertx);
		LightDTService lightService = new LightDTService(lightModel);
		vertx.deployVerticle(lightService);
	}

}
