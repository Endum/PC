package smart_room.distributed;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class LightDeviceAgent extends Thread{
    
    private final static Double THRESHOLD = 0.5;

    private String lightTopic;
    private String detectionTopic;
    private int qos;
    private MemoryPersistence persistence = new MemoryPersistence();
    private String broker;
    private String clientId = "LightDeviceAgent";
    private LightDeviceSimulator lightDevice = new LightDeviceSimulator(clientId);

    private boolean detection = false;
    private Double light = 100.0;

    public LightDeviceAgent(String lightTopic, String detectionTopic, String broker, int qos) {
    	this.broker = broker;
    	this.qos = qos;
    	this.lightTopic = lightTopic;
    	this.detectionTopic = detectionTopic;
    }

    @Override
    public void run(){
        try {
            MqttClient client = new MqttClient(broker, clientId, persistence);
	        MqttConnectOptions connOpts = new MqttConnectOptions();
	        connOpts.setCleanSession(true);
            log("Connecting to broker: "+broker);
	        client.connect(connOpts);
	        log("Connected");

            client.subscribe(lightTopic, (top, msg) -> {
			    byte[] payload = msg.getPayload();
                String message = new String(payload);
			    log("received light -> " + message);
                light = Double.parseDouble(message);

                checkAndAct();
			});

            client.subscribe(detectionTopic, (top, msg) -> {
                byte[] payload = msg.getPayload();
                String message = new String(payload);
			    log("received detection -> " + message);
                detection = Boolean.parseBoolean(message);

                checkAndAct();
            });

            while (client.isConnected()) {
                Thread.sleep(100);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void log(String msg) {
		System.out.println("[LightDeviceAgent] " + msg);
	}

    private void checkAndAct() {
        if(detection && light < THRESHOLD) {
            log("light turned on");
            lightDevice.on();
        } else {
            log("light turned off");
            lightDevice.off();
        }
    }
}
