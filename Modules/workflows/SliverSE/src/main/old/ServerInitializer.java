package main.old;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;

import org.xmlpull.v1.XmlPullParserException;

import edu.wustl.mobilab.sliver.bpel.BPELServer;
import edu.wustl.mobilab.sliver.soap.Channel;
import edu.wustl.mobilab.sliver.soap.SOAPServer;
import edu.wustl.mobilab.sliver.soap.Serialization;
import edu.wustl.mobilab.sliver.soap.Transport;
import edu.wustl.mobilab.sliver.soap.j2se.MethodCallHandler;
import edu.wustl.mobilab.sliver.soap.j2se.SocketTransport;
import edu.wustl.mobilab.sliver.soap.j2se.SocketTransport.Endpoint;
import edu.wustl.mobilab.sliver.util.MalformedDocumentException;

public class ServerInitializer {

	public void startServer(){
		Serialization.registerClass("http://some/Namespace", MyCustomType.class);
		
		Transport transport = null;
		try {
			transport = new SocketTransport(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Starts a connection to a remote machine!
		Endpoint point = null;
		try {
			point = new Endpoint("192.168.0.1", 23);
		} catch (UnknownHostException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			transport.openChannel(point);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		SOAPServer server = new SOAPServer(transport);
		server.registerService("http://some/Namespace", new MethodCallHandler(MyService.class));
		
		try {
			server.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("The SOAP Server is started!");
		
		
		/**
		try {
			server.stop();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("The SOAP Server is started!");
		**/
		
		
		BPELServer bpelserver = new BPELServer(transport);
		try {
			bpelserver.addProcess("http://some/Namespace", new FileInputStream("SomeProcess.bpel"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (XmlPullParserException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (MalformedDocumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		System.out.println(bpelserver.toString());
		
		//Channel channel = new MyCustomChannel();
		
		//bpelserver.newConnection(channel);
		
	}
	
}
