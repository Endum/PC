package lamp_thing.impl;

import java.util.Iterator;
import java.util.LinkedList;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import lamp_thing.api.DetectionThingAPI;

public class PresenceThingHTTPAdapter extends ThingAbstractAdapter<DetectionThingAPI> {

	private HttpServer server;
	private Router router;

	private String thingHost;
	private int thingPort;

	private static final String TD = "/api";
	private static final String PROPERTY_DETECTION = "/api/properties/detection";
	private static final String EVENTS = "/api/events";

	// event support
	private LinkedList<ServerWebSocket> subscribers;

	public PresenceThingHTTPAdapter(DetectionThingAPI model, String host, int port, Vertx vertx) {
		super(model, vertx);
		this.thingHost = host;
		this.thingPort = port;
	}

	protected void setupAdapter(Promise<Void> startPromise) {
		Future<JsonObject> tdfut = this.getModel().getTD();
		tdfut.onComplete(tdres -> {
			JsonObject td = tdres.result();

			router = Router.router(this.getVertx());
			try {
				router.get(TD).handler(this::handleGetTD);
				router.get(PROPERTY_DETECTION).handler(this::handleGetPropertyDetection);

				populateTD(td);

			} catch (Exception ex) {
				log("API setup failed - " + ex.toString());
				startPromise.fail("API setup failed - " + ex.toString());
				return;
			}

			subscribers = new LinkedList<ServerWebSocket>();

			this.getModel().subscribe(ev -> {
				Iterator<ServerWebSocket> it = this.subscribers.iterator();
				while (it.hasNext()) {
					ServerWebSocket ws = it.next();
					if (!ws.isClosed()) {
						try {
							ws.write(ev.toBuffer());
						} catch (Exception ex) {
							it.remove();
						}
					} else {
						it.remove();
					}
				}
			});

			server = this.getVertx().createHttpServer();
			server.webSocketHandler(ws -> {
				if (!ws.path().equals(EVENTS)) {
					ws.reject();
				} else {
					log("New subscriber from " + ws.remoteAddress());
					subscribers.add(ws);
				}
			}).requestHandler(router).listen(thingPort, http -> {
				if (http.succeeded()) {
					startPromise.complete();
					log("HTTP Thing Adapter started on port " + thingPort);
				} else {
					log("HTTP Thing Adapter failure " + http.cause());
					startPromise.fail(http.cause());
				}
			});
		});
	}

	/**
	 * Configure the TD with the specific bindings provided by the adapter
	 * 
	 * @param td
	 */
	protected void populateTD(JsonObject td) {
		JsonArray detectionForms = 
				td
				.getJsonObject("properties")
				.getJsonObject("detection")
				.getJsonArray("forms");

		JsonObject httpDetectionForm = new JsonObject();
		httpDetectionForm.put("href", "http://" + thingHost + ":" + thingPort + "/api/properties/detection");
		detectionForms.add(httpDetectionForm);

		JsonArray detectionChangedForms = 
				td
				.getJsonObject("events")
				.getJsonObject("detectionChanged")
				.getJsonArray("forms");

		JsonObject httpDetectionChangedForm = new JsonObject();
		httpDetectionChangedForm.put("href", "http://" + thingHost + ":" + thingPort + "/api/events");
		detectionChangedForms.add(httpDetectionChangedForm);
	}

	protected void handleGetPropertyDetection(RoutingContext ctx) {
		HttpServerResponse res = ctx.response();
		res.putHeader("Content-Type", "application/json");
		JsonObject reply = new JsonObject();
		Future<String> fut = this.getModel().getDetection();
		fut.onSuccess(detection -> {
			reply.put("detection", detection);
			res.end(reply.toBuffer());
		});
	}

	protected void handleGetTD(RoutingContext ctx) {
		HttpServerResponse res = ctx.response();
		res.putHeader("Content-Type", "application/json");
		Future<JsonObject> fut = this.getModel().getTD();
		fut.onSuccess(td -> {
			res.end(td.toBuffer());
		});
	}

	protected void log(String msg) {
		System.out.println("[PresenceThingHTTPAdapter][" + System.currentTimeMillis() + "] " + msg);
	}

}
