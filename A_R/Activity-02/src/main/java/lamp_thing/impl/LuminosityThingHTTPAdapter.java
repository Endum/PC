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
import lamp_thing.api.LuminosityThingAPI;

public class LuminosityThingHTTPAdapter extends ThingAbstractAdapter<LuminosityThingAPI> {

	private HttpServer server;
	private Router router;

	private String thingHost;
	private int thingPort;

	private static final String TD = "/api";
	private static final String PROPERTY_LUMINOSITY = "/api/properties/luminosity";
	private static final String EVENTS = "/api/events";

	// event support
	private LinkedList<ServerWebSocket> subscribers;

	public LuminosityThingHTTPAdapter(LuminosityThingAPI model, String host, int port, Vertx vertx) {
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
				router.get(PROPERTY_LUMINOSITY).handler(this::handleGetPropertyLuminosity);

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
		JsonArray luminosityForms = 
				td
				.getJsonObject("properties")
				.getJsonObject("luminosity")
				.getJsonArray("forms");

		JsonObject httpluminosityForm = new JsonObject();
		httpluminosityForm.put("href", "http://" + thingHost + ":" + thingPort + "/api/properties/luminosity");
		luminosityForms.add(httpluminosityForm);

		JsonArray luminosityChangedForms = 
				td
				.getJsonObject("events")
				.getJsonObject("luminosityChanged")
				.getJsonArray("forms");

		JsonObject httpluminosityChangedForm = new JsonObject();
		httpluminosityChangedForm.put("href", "http://" + thingHost + ":" + thingPort + "/api/events");
		luminosityChangedForms.add(httpluminosityChangedForm);
	}

	protected void handleGetPropertyLuminosity(RoutingContext ctx) {
		HttpServerResponse res = ctx.response();
		res.putHeader("Content-Type", "application/json");
		JsonObject reply = new JsonObject();
		Future<String> fut = this.getModel().getLuminosity();
		fut.onSuccess(luminosity -> {
			reply.put("luminosity", luminosity);
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
		System.out.println("[LuminosityThingHTTPAdapter][" + System.currentTimeMillis() + "] " + msg);
	}

}
