package main.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class BPELParser {
	
	String xmlString;
	String[] results = new String[10];
	
	
	public BPELParser(String xmlString){
		this.xmlString = xmlString;
	}
	
	public String[] query(String query) throws XmlPullParserException, IOException{
		parseXML(query);
		return results;
	}
	
	/**
	 * Will add the result String to the results String array.
	 * If there are no empty slots, and the array has been looped thru, it will be expanded, and the result String added.
	 * @param result
	 */
	private void addToResult(String result){
		
		
		
		for(int i=0;i<results.length;i++){
			if(results[i] == null){
				results[i] = result;
				break;
			}else if(i == (results.length - 1)){
				String[] oldResultsList = results;
				results = new String[results.length + 10];
				for(int j=0;j<oldResultsList.length;j++){
					results[j] = oldResultsList[j];
				}
				/**
				 * This works because the list has just been checked, and was full.
				 * We then expanded it and added everything from the full list, the first new space on the new list will therefor be empty.
				 */
				results[oldResultsList.length] = result;
				
				break;
			}
		}
	}
	
	private void parseXML(String query) throws XmlPullParserException, IOException{
		/**
         * Converts the input in a way so the KXml parser will accept it.
         */
        byte[] xmlByteArray = xmlString.getBytes();
        ByteArrayInputStream xmlStream = new ByteArrayInputStream(xmlByteArray);
        InputStreamReader xmlReader = new InputStreamReader(xmlStream);
        KXmlParser parser = new KXmlParser();
        
        //Takes the now converted DataInputStream and sets it as the input for the parser.
        parser.setInput(xmlReader);
        
        //This will tell the loop when to end. 
        //Its set to the initial value here and changed during the loop.
        int eventType = parser.getEventType();
        
        //Loops thru the entire XML/BPEL document and prints it out.
        while(eventType != XmlPullParser.END_DOCUMENT){
        
        	/**
        	if(eventType == XmlPullParser.START_DOCUMENT){
        		System.out.println("Start document");
        	}else if(eventType == XmlPullParser.END_DOCUMENT){
        		System.out.println("End document");
        	}else if(eventType == XmlPullParser.START_TAG){
        		System.out.println("Start tag " + parser.getName());
        	}else if(eventType == XmlPullParser.END_TAG){
        		System.out.println("End tag " + parser.getName());
        	}else if(eventType == XmlPullParser.TEXT){
        		System.out.println("Text " + parser.getText());
        	}
        	**/
        	
        	/**
        	 * Checks if the start tag is equal to the query text.
        	 * The text contained after it is added to the result list 
        	 */
        	if(eventType == XmlPullParser.START_TAG){
        		if(parser.getName().equals(query)){
        			//addToResult(parser.getName());
        			eventType = parser.next();
        			addToResult(parser.getText());
        		}
        		eventType = parser.next();
        	}else{
        		eventType = parser.next();
        	}
        	
        	//eventType = parser.next();
        }
	}
	
}
