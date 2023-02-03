package smart_room.distributed;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class LightSensorAgent extends Thread{
    private String topic;
    private int qos;
    private MemoryPersistence persistence = new MemoryPersistence();
    private String broker;
    private String clientId = "LightSensorAgent";

    private LuminositySensorSimulator sensor = new LuminositySensorSimulator(clientId);

    private Double lastLight = 0.0;

    public LightSensorAgent(String topic, String broker, int qos) {
    	this.broker = broker;
    	this.qos = qos;
    	this.topic = topic;
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

            while(client.isConnected()) {
                if(lastLight != sensor.getLuminosity()) {
                    lastLight = sensor.getLuminosity();
                    String content = String.valueOf(lastLight);
                    log("Publishing light: " + content);
                    MqttMessage message = new MqttMessage(content.getBytes());
                    message.setQos(qos);
                    client.publish(topic, message);
                    log("Light " + content + " published");	
                    Thread.sleep(100);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void log(String msg) {
		System.out.println("[LightSensorAgent] " + msg);
	}
}
