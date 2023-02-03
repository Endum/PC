package smart_room.distributed;

import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import mqtt_tests.MQTTVertxServer;

public class MultipleBoardSimulator {
    public static void main(String[] args) {
        String lightTopic       = "light_topic";
        String detectionTopic   = "detection_topic";
        int qos                 = 2;
        String broker           = "tcp://localhost:1883";
        MemoryPersistence persistence = new MemoryPersistence();

        MQTTVertxServer.main(args);

        new DetectionSensorAgent(detectionTopic, broker, qos).start();

        new LightSensorAgent(lightTopic, broker, qos).start();
    
        new LightDeviceAgent(lightTopic, detectionTopic, broker, qos).start();
    }
}
