package acme;

import cartago.*;
import io.vertx.core.json.JsonObject;


public class DetectorThingProxyArtifact extends ThingProxyArtifact {

	private String uri;
	
	private static final String THING_BASE_PATH = "/api";
	private static final String PROPERTIES_BASE_PATH = THING_BASE_PATH + "/properties";
	private static final String PROPERTY_STATE = "detection";
	private static final String EVENT_NAME = "detectionChanged";
	private static final String PROPERTY_STATE_FULL_PATH = PROPERTIES_BASE_PATH + "/" + PROPERTY_STATE;

	public void init(String host, int port) throws Exception {
		super.init(host, port);
		this.uri = "http://"+ host + ":" + port;
		bindToPhysicalThing();
		log("ready.");
	}


	private void bindToPhysicalThing() throws Exception {
		
		log("connecting to " + uri);

		/* read initial state */
		
		JsonObject temp = this.doGetBlocking(uri + PROPERTY_STATE_FULL_PATH);
		defineObsProperty(PROPERTY_STATE, temp.getString(PROPERTY_STATE));
		
		/* subscribe */
		
		this.doSubscribe(EVENT_NAME, PROPERTY_STATE);
		
	}	

	@OPERATION void getCurrentDetection(OpFeedbackParam<String> state) {
		try {
			log("getting the detection.");
			JsonObject obj = this.doGetBlocking(uri + PROPERTY_STATE_FULL_PATH);
			state.set(obj.getString(PROPERTY_STATE));
		} catch (Exception ex) {
			failed("");
		}
	}

}
