package smart_room.distributed;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class DetectionSensorAgent extends Thread{
    private String topic;
    private int qos;
    private MemoryPersistence persistence = new MemoryPersistence();
    private String broker;
    private String clientId = "DetectionSensorAgent";

    private PresDetectSensorSimulator sensor = new PresDetectSensorSimulator(clientId);
    private boolean lastDetect = false;

    public DetectionSensorAgent(String topic, String broker, int qos) {
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

            while (client.isConnected()) {
                if(lastDetect != sensor.presenceDetected()) {
                    lastDetect = sensor.presenceDetected();
                    String content = String.valueOf(sensor.presenceDetected());
                    log("Publishing detected: " + content);
                    MqttMessage message = new MqttMessage(content.getBytes());
                    message.setQos(qos);
                    client.publish(topic, message);
                    log("Detected " + content + " published");	
                    Thread.sleep(100);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void log(String msg) {
		System.out.println("[DetectionSensorAgent] " + msg);
	}
}
