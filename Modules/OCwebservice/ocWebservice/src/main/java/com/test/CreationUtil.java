package com.test;

import java.io.*;
import org.w3c.dom.Document;
import java.io.File;
import java.io.FileOutputStream;
import javax.xml.parsers.DocumentBuilder;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
/** 
 *  .
 * @author gbro
 *
 */

public class CreationUtil {

	
	public CreationUtil()
	{
		
	}
	
	/**
	 * Using JAXP in implementation independent manner create a document object
	 * using which we create a xml tree in memory
	 */
	
	public Document createDocument( Document dom) {

		//get an instance of factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
		//get an instance of builder
		DocumentBuilder db = dbf.newDocumentBuilder();

		//create an instance of DOM
		dom = db.newDocument();
		
		

		}catch(ParserConfigurationException pce) {
			//dump it
			System.out.println("Error while trying to instantiate DocumentBuilder " + pce);
			System.exit(1);
		}
		return dom;
	}
	
	/**
	 * This method uses Xerces specific classes
	 * prints the XML document to file.
     */
	public void printToFile(Document dom, String filename)
	{

		try
		{
			//print
			com.sun.org.apache.xml.internal.serialize.OutputFormat format = new OutputFormat(dom);
			format.setIndenting(true);

			//to generate output to console use this serializer
			//XMLSerializer serializer = new XMLSerializer(System.out, format);

			
			//to generate a file output use fileoutputstream instead of system.out
			XMLSerializer serializer = new XMLSerializer (
			new FileOutputStream(new File(filename)), format);

			serializer.serialize(dom);
			
		} catch(IOException ie)
		{
		    ie.printStackTrace();
		}
	}
	
	/**
	 * This method gets the content of the xml file created on the file system and returns 
	 * it as astring 
	 * it takes the xml file created on the file system
     */
	
	 public static String getContents(File aFile) {
	    //...checks on aFile are elided
	    StringBuilder contents = new StringBuilder();
	    
	    try {
	      //use buffering, reading one line at a time
	      //FileReader always assumes default encoding is OK!
	      BufferedReader input =  new BufferedReader(new FileReader(aFile));
	      
	      try {
	        String line = null; //not declared within while loop
	        
	        /*
	        * it returns the content of a line MINUS the newline.
	        * it returns null only for the END of the stream.
	        * it returns an empty String if two newlines appear in a row.
	        */
	        
	        while (( line = input.readLine()) != null){
	          contents.append(line);
	          contents.append(System.getProperty("line.separator"));
	        }
	      }
	      finally {
	        input.close();
	      }
	    }
	    catch (IOException ex){
	      ex.printStackTrace();
	    }
	    
	    return contents.toString();
	  }

	
}
