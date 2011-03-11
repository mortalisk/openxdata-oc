package org.openxdata.client;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Event that is triggered by a Refresh and indicates
 * what type of refresh should take place.
 *
 * Also allows data to be passed along to the View
 * consuming the event.
 *
 * @author dagmar@cell-life.org.za
 */
public class RefreshableEvent implements Serializable  {
	
	private static final long serialVersionUID = 7549150917841490121L;

	public static enum Type { CAPTURE, NAME_CHANGE, FORM_DATA_UPDATE, UPDATE_STUDY, CREATE_STUDY, DELETE };

	private Type eventType;
	private Map<String, Object> data = new HashMap<String, Object>();
	
	public RefreshableEvent(Type eventType) {
		this.eventType = eventType;
	}
	
	public RefreshableEvent(Type eventType, Object data) {
		this(eventType);
		this.data.put("data", data);
	}
	
	@SuppressWarnings("unchecked")
	public <X> X getData() {
		return (X) data.get("data");
	}
	
	@SuppressWarnings("unchecked")
	public <X> X getData(String key) {
		return (X) data.get(key);
	}
	
	public void setData(Object data) {
		this.data.put("data", data);
	}
	
	public void setData(String key, Object data) {
		this.data.put(key, data);
	}
	
	public Type getEventType() {
		return eventType;
	}
}
