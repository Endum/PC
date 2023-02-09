import java.util.Arrays;
import java.util.List;

import room.consumer.SmartRoom;

public class SmartBuilding {
    
    public static void main(String[] args) {

        List<SmartRoom> smartRooms = Arrays.asList(
            new SmartRoom("192.168.0.10", "192.168.0.11", "192.168.0.12"),
            new SmartRoom("192.168.0.20", "192.168.0.21", "192.168.0.22"),
            new SmartRoom("192.168.0.30", "192.168.0.31", "192.168.0.32")
        );

        for (SmartRoom sr : smartRooms) {
            sr.subscribe(System.out::println);
        }
        
        smartRooms.get(0).turnOn();
        smartRooms.get(1).getState().andThen(System.out::println);
        smartRooms.get(2).getState().andThen(System.out::println);
    }

}
