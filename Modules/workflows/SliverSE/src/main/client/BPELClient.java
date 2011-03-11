package main.client;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import org.kxml2.io.KXmlParser;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import edu.wustl.mobilab.sliver.bpel.Process;

public class BPELClient {
	
	/**
	 * XML example: "hellotest.xml"
	 */
	private String xmlInputFile;
	private String hostIP;
	private int hostPort;
	Process process;
	String SOAPResponse;
	
	
	public BPELClient(String xmlInputFile, String hostIP, int hostPort) throws IOException{
		this.xmlInputFile = xmlInputFile;
		this.hostIP = hostIP;
		this.hostPort = hostPort;
		getServerSOAPResponse();
	}
	
	/**
	 * This method basically connects to the server and retrieves the BPEL file it sends.
	 * @throws IOException
	 */
	private void getServerSOAPResponse() throws IOException{
		InputStream fis = new FileInputStream(xmlInputFile);
        BufferedReader bis = new BufferedReader(new InputStreamReader(fis));
        String read = null;
        StringBuffer bf = new StringBuffer();

        //PrintStream sout = System.out;
        while ((read = bis.readLine()) != null)
        {
            bf.append(read).append('\n');
        }

        //System.out.println(bf);
        
        //Opens a socket to the remote server.
        final Socket socket = new Socket(hostIP, hostPort);
        
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        out.writeUTF(bf.toString());

        DataInputStream din = new DataInputStream(socket.getInputStream());
       
        //Saves the BPEL file in a String for further use.
        SOAPResponse = din.readUTF();
        
        //Do something with the response
        
        //Return result
        //out.writeUTF(bf.toString());
        
        out.close();
        din.close();
        fis.close();
	}
	
	/**
	 * This method creates a BPEL parser which will do a tag search based on the query string on the BPEL variable.
	 * @param query
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public String[] queryBPEL(String query) throws XmlPullParserException, IOException{
		BPELParser parser = new BPELParser(SOAPResponse);
		
		return parser.query(query);
	}
	
	public Process getProcess(){
		return process;
	}
	
	/**
	 * This method is kept as a reference and for testing temporarily!
	 * @throws IOException
	 * @throws XmlPullParserException
	 * @throws SAXException
	 */
	public void startClient() throws IOException, XmlPullParserException, SAXException{
		InputStream fis = new FileInputStream(xmlInputFile);
        BufferedReader bis = new BufferedReader(new InputStreamReader(fis));
        String read = null;
        StringBuffer bf = new StringBuffer();

        PrintStream sout = System.out;
        while ((read = bis.readLine()) != null)
        {
            bf.append(read).append('\n');
        }

        //System.out.println(bf);
        final Socket socket = new Socket(hostIP, hostPort);

        DataOutputStream out =
                new DataOutputStream(socket.getOutputStream());

        out.writeUTF(bf.toString());

        DataInputStream din = new DataInputStream(socket.getInputStream());
        String readUTF = din.readUTF();
        
        /**
         * Converts the input in a way so the KXml parser will accept it.
         */
        byte[] xmlByteArray = readUTF.getBytes();
        ByteArrayInputStream xmlStream = new ByteArrayInputStream(xmlByteArray);
        InputStreamReader xmlReader = new InputStreamReader(xmlStream);
        KXmlParser parser = new KXmlParser();
        
        //Takes the now converted DataInputStream and sets it as the input for the parser.
        parser.setInput(xmlReader);
        
        //This will tell the loop when to end. 
        //Its set to the initial value here and changed during the loop.
        int eventType = parser.getEventType();
        
        int counter = 0;
        
        //Loops thru the entire XML/BPEL document and prints it out.
        while(eventType != XmlPullParser.END_DOCUMENT){
        	if(eventType == XmlPullParser.START_DOCUMENT){
        		System.out.println("Start document");
        		System.out.println(counter);
        	}else if(eventType == XmlPullParser.END_DOCUMENT){
        		System.out.println("End document");
        		System.out.println(counter);
        	}else if(eventType == XmlPullParser.START_TAG){
        		System.out.println("Start tag " + parser.getName());
        		System.out.println(counter);
        	}else if(eventType == XmlPullParser.END_TAG){
        		System.out.println("End tag " + parser.getName());
        		System.out.println(counter);
        	}else if(eventType == XmlPullParser.TEXT){
        		System.out.println("Text " + parser.getText());
        		System.out.println(counter);
        	}
        	
        	counter++;
        	eventType = parser.next();
        }
        
        
        
        //sout.println(readUTF);
        
        out.close();
        din.close();
        fis.close();
	}
}
