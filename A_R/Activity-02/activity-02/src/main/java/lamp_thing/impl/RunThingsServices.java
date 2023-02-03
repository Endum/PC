package lamp_thing.impl;

import io.vertx.core.Vertx;

public class RunThingsServices {
    public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();

		LampThingModel lampModel = new LampThingModel("MyLamp");
		lampModel.setup(vertx);
		
		LampThingService lampService = new LampThingService(lampModel);
		vertx.deployVerticle(lampService);


        LuminosityThingModel lumiModel = new LuminosityThingModel("MyLumi");
        lumiModel.setup(vertx);

        LuminosityThingService lumiService = new LuminosityThingService(lumiModel);
        vertx.deployVerticle(lumiService);


        PresenceThingModel presModel = new PresenceThingModel("MyPres");
        presModel.setup(vertx);

        PresenceThingService presService = new PresenceThingService(presModel);
        vertx.deployVerticle(presService);

    }
}
