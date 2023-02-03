package lamp_thing.consumers;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.WebSocket;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import lamp_thing.api.ThingAPI;

public abstract class ThingHTTPProxy implements ThingAPI{

    protected Vertx vertx;
	protected WebClient client;

    protected String thingName;
	protected int thingPort;
	protected String thingHost;

	protected static final String TD = "/api";
	protected static final String EVENTS = "/api/events";

    public ThingHTTPProxy(String thingName, String thingHost, int thingPort) {
        this.thingName = thingName;
        this.thingHost = thingHost;
		this.thingPort = thingPort;
    }

    public Future<Void> setup(Vertx vertx) {
		this.vertx = vertx;
		Promise<Void> promise = Promise.promise();
		vertx.executeBlocking(p -> {
			client = WebClient.create(vertx);
			promise.complete();
		});
		return promise.future();
	}

    @Override
    public Future<JsonObject> getTD() {
        Promise<JsonObject> promise = Promise.promise();
		client
			.get(this.thingPort, thingHost, TD)
			.send()
			.onSuccess(response -> {
				JsonObject reply = response.bodyAsJsonObject();
				promise.complete(reply);
			})
			.onFailure(err -> {
				promise.fail("Something went wrong " + err.getMessage());
			});
		return promise.future();
    }

    @Override
    public Future<Void> subscribe(Handler<JsonObject> handler) {
        Promise<Void> promise = Promise.promise();
		HttpClient cli = vertx.createHttpClient();
		cli.webSocket(this.thingPort, thingHost, EVENTS, res -> {
			if (res.succeeded()) {
				log("Connected!");
				WebSocket ws = res.result();
				ws.handler(buf -> {
					handler.handle(buf.toJsonObject());
				});
				promise.complete();
			}
		});
		return promise.future();
    }

    protected void log(String msg) {
		System.out.println("["+ this.thingName +"]["+System.currentTimeMillis()+"] " + msg);
	}
    
}
