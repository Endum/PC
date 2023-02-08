package room.dt.light;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.mqtt.MqttClient;

/**
 * Light sensor DT in-bound Shadowing module, based on MQTT protocol
 * 
 * @author aricci
 *
 */
public class LightDTShadMQTT  {

	private Vertx vertx;

    private MqttClient client;	
    private final int qos = 1;
	private String host;
	private int port;
	
	private LightDTShadAPI model;
	private String dtId;
	private String thingId;
	
	private String shadowingTopicFromPAtoDT;
	private String shadowingTopicFromDTtoPA;
	
		
	public LightDTShadMQTT(LightDTShadAPI model, String dtId, String thingId, String host, int port) throws Exception {
		this.model = model;
		this.thingId = thingId;
		this.dtId = dtId;
		this.host = host;
		this.port = port;
	}
	
	
	public Future<Void> setup(Vertx vertx) {
		this.vertx = vertx;
		Promise<Void> promise = Promise.promise();
		client = MqttClient.create(vertx);
        this.shadowingTopicFromPAtoDT = "dt/" +  dtId + "/shadowing";
        this.shadowingTopicFromDTtoPA = "pa/" +  thingId +"/shadowing";

		client.connect(port, host, c -> {
			log("MQTT DT shadowing module connected - in topic: " + shadowingTopicFromPAtoDT + " - out topic: " + shadowingTopicFromDTtoPA);
			
			/* PA shad messages */
			client.publishHandler(s -> {
				
		    	this.vertx.eventBus().publish("shadowing", new String(s.payload().toString()));    	
			})
			.subscribe(shadowingTopicFromPAtoDT, qos);

	        /* handler to process MQTT msgs on the event-loop */ 
	        vertx.eventBus().consumer("shadowing", this::handleShadowing);   
	        
	        /* initial sync request */
	        JsonObject msgSync = new JsonObject();
	        msgSync.put("type", "syncRequest");
	        sendMsg(shadowingTopicFromDTtoPA, msgSync);
	        
	        promise.complete();

		});
		
		
        return promise.future();
	        
	}
	
	private void sendMsg(String topic, JsonObject msg) {
		client.publish(topic,
				  Buffer.buffer(msg.encode()),
				  MqttQoS.AT_LEAST_ONCE,
				  false,
				  false);
	}
	
	private void handleShadowing(Message<String> msg) {
		log("Processing new shadowing: " + msg.body());
		JsonObject obj = new JsonObject(msg.body());
		String msgType = obj.getString("msg");
		if (msgType.equals("sync-event")) {
			JsonObject syncData = obj.getJsonObject("syncData");
			String ev = syncData.getString("event");
			long timeStamp = syncData.getLong("timestamp");
			if (ev.equals("stateChanged")) {
				JsonObject data = syncData.getJsonObject("data");
				String state = data.getString("state");
				this.model.updateState(state, timeStamp);
			} else {
				log("unknown shadowing event: " + ev);
			}
		} else if (msgType.equals("sync-state")) {
			JsonObject syncData = obj.getJsonObject("syncData");
			long timeStamp = syncData.getLong("timestamp");
			String state = syncData.getString("state");
			this.model.updateState(state, timeStamp);
		} else {
			log("unknown msg: " + msgType);
		}
	}	
	
	protected void log(String msg) {
		System.out.println("[LightDTShadMQTT]["+System.currentTimeMillis()+"] " + msg);
	}
	
}
