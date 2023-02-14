package acme;

import java.util.Optional;
import cartago.*;
import io.vertx.core.json.JsonObject;


public class LampThingProxyArtifact extends ThingProxyArtifact {

	private String uri;
	
	private static final String THING_BASE_PATH = "/api";
	private static final String PROPERTIES_BASE_PATH = THING_BASE_PATH + "/properties";
	private static final String PROPERTY_STATE = "state";
	private static final String EVENT_NAME = "stateChanged";
	private static final String PROPERTY_STATE_FULL_PATH = PROPERTIES_BASE_PATH + "/" + PROPERTY_STATE;
	private static final String ACTIONS_BASE_PATH = THING_BASE_PATH + "/actions";
	private static final String ACTION_ON = "on";
	private static final String ACTION_ON_FULL_PATH = ACTIONS_BASE_PATH + "/" + ACTION_ON;
	private static final String ACTION_OFF = "off";
	private static final String ACTION_OFF_FULL_PATH = ACTIONS_BASE_PATH + "/" + ACTION_OFF;

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

	@OPERATION void getCurrentState(OpFeedbackParam<String> state) {
		try {
			log("getting the state.");
			JsonObject obj = this.doGetBlocking(uri + PROPERTY_STATE_FULL_PATH);
			state.set(obj.getString(PROPERTY_STATE));
		} catch (Exception ex) {
			failed("");
		}
	}

	@OPERATION void on() {
		try {
			log("on...");
			this.doPostBlocking(uri + ACTION_ON_FULL_PATH,Optional.empty());
		} catch (Exception ex) {
			ex.printStackTrace();
			failed(ex.getMessage());
		}
	}

	@OPERATION void off() {
		try {
			log("off...");
			this.doPostBlocking(uri + ACTION_OFF_FULL_PATH,Optional.empty());
		} catch (Exception ex) {
			failed(ex.getMessage());
		}
	}

}
