package smart_room.distributed;

import smart_room.Controller;
import smart_room.Event;

public class MyController implements Controller {

	@Override
	public void notifyEvent(Event ev) {
		System.out.println("New event: " + ev);
	}

	
}
