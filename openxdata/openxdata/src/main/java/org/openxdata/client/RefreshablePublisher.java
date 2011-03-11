package org.openxdata.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Publisher that is used to trigger Events so
 * subscribers can be notified.
 *
 * @author dagmar@cell-life.org.za
 */
public class RefreshablePublisher {
	
	private static RefreshablePublisher instance = new RefreshablePublisher();
	Map<RefreshableEvent.Type, ArrayList<Refreshable>> subscribers = new HashMap<RefreshableEvent.Type, ArrayList<Refreshable>>();
	
	private RefreshablePublisher() {}

	public static RefreshablePublisher get() {
		return instance;
	}
	
	public void subscribe(RefreshableEvent.Type type, Refreshable refreshable) {
		ArrayList<Refreshable> subs = subscribers.get(type); 
		if (subs == null) {
			subs = new ArrayList<Refreshable>();
			subscribers.put(type, subs);
		}
		subs.add(refreshable);
	}
	
	public void publish(RefreshableEvent event) {
		ArrayList<Refreshable> subs = subscribers.get(event.getEventType());
		if (subs != null) {
			for (Refreshable refreshable : subs) {
				refreshable.refresh(event);
			}
		}
	}
}