package acme;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Optional;

import cartago.*;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;


public class ThingProxyArtifact extends Artifact {

    private static final String THING_BASE_PATH = "/api";
	private static final String EVENTS_FULL_PATH = THING_BASE_PATH + "/events";

    private String host;
	private int port;
	private HttpClient client;

    public void init(String host, int port) throws Exception{
        this.host = host;
        this.port = port;
        this.client = HttpClient.newHttpClient();
    }

    protected JsonObject doGetBlocking(String uri) throws Exception {
		try {
			var request = HttpRequest.newBuilder()
					.uri(URI.create(uri))
					.build();		

			var response = client.send(request,  BodyHandlers.ofString());
			return new JsonObject(response.body());
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	protected JsonObject doPostBlocking(String uri, Optional<JsonObject> body) throws Exception {
		HttpRequest req = null;
		// log("doing a post at " + "http://" + uri);
		if (!body.isEmpty()) {
			req  = HttpRequest.newBuilder()
					.uri(URI.create(uri))
					.POST(BodyPublishers.ofString(body.get().toString()))
					.build();		

		} else {
			req = HttpRequest.newBuilder()
					.uri(URI.create(uri))
					.POST(BodyPublishers.noBody())
					.build();		
		}

		var response = client.send(req, BodyHandlers.ofString());
		if(response.body().length() > 0) {
			return new JsonObject(response.body());
		}
		return new JsonObject();
	}
	
	protected void doSubscribe(String event, String state) {
		Vertx vertx = Vertx.vertx();
		ThingProxyArtifact art = this;
		log("Subscribing...");
		vertx.createHttpClient().webSocket(port, host, EVENTS_FULL_PATH, ws -> {	
			/* handling ws msgs */
			log("Connected!");
			ws.result().handler(msg -> {
				try {
					JsonObject ev = new JsonObject(msg.toString());		    	  
					String msgType = ev.getString("event");
					if (msgType.equals(event)) {
						JsonObject data = ev.getJsonObject("data");
						String newState = data.getString(state);

						// log("updating artifact state with " + newTemperature + " " + newState);

						art.beginExtSession();							

						if (newState != null) {
							ObsProperty sprop = getObsProperty(state);
							sprop.updateValue(newState);
						}
						art.endExtSession();
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			});		
		});
		
	}
}