package org.openxdata.client.views;

import org.openxdata.client.AppMessages;

import com.google.gwt.core.client.GWT;

public class ItemAccessListFieldMessages {
	
	private String leftHeading;
	private String rightHeading;
	private String addOne;
	private String addAll;
	private String removeOne;
	private String removeAll;
	private String search;
	private String loading;

	AppMessages appMessages = GWT.create(AppMessages.class);
    
	public ItemAccessListFieldMessages() {
    	loadDefaultMessages();
    }
    
    public ItemAccessListFieldMessages(String messages) {
    	loadDefaultMessages();
    	String[] lines = messages.split("\n");
    	for (String line : lines) {
    		String[] props = line.split("=");
    		setValue(props[0].trim(), props[1].trim());
    	}
    }
    
    private void setValue(String propName, String propValue) {
    	if (propName.equals("leftHeading")) setLeftHeading(propValue);
		else if (propName.equals("rightHeading")) setRightHeading(propValue);
		else if (propName.equals("addOne")) setAddOne(propValue); 
		else if (propName.equals("addAll")) setAddAll(propValue); 
		else if (propName.equals("removeOne")) setRemoveOne(propValue); 
		else if (propName.equals("removeAll")) setRemoveAll(propValue); 
		else if (propName.equals("search")) setSearch(propValue); 
		else if (propName.equals("loading")) setLoading(propValue); 
		else GWT.log("Unknown property with name "+propName);
    }
    
    public void loadDefaultMessages() {
    	setLeftHeading("Available items");
    	setRightHeading("Assigned items");
    	setAddOne(">");
    	setAddAll(">>");
    	setRemoveOne("<");
    	setRemoveAll("<<");
    	setSearch("Search");
    	setLoading(appMessages.loading());
    }

	public String getLeftHeading() {
    	return leftHeading;
    }

	public void setLeftHeading(String leftHeading) {
    	this.leftHeading = leftHeading;
    }

	public String getRightHeading() {
    	return rightHeading;
    }

	public void setRightHeading(String rightHeading) {
    	this.rightHeading = rightHeading;
    }

	public String getAddOne() {
    	return addOne;
    }

	public void setAddOne(String addOne) {
    	this.addOne = addOne;
    }

	public String getAddAll() {
    	return addAll;
    }

	public void setAddAll(String addAll) {
    	this.addAll = addAll;
    }

	public String getRemoveOne() {
    	return removeOne;
    }

	public void setRemoveOne(String removeOne) {
    	this.removeOne = removeOne;
    }

	public String getRemoveAll() {
    	return removeAll;
    }

	public void setRemoveAll(String removeAll) {
    	this.removeAll = removeAll;
    }

	public String getSearch() {
    	return search;
    }

	public void setSearch(String search) {
    	this.search = search;
    }

	public String getLoading() {
    	return loading;
    }

	public void setLoading(String loading) {
    	this.loading = loading;
    }
}