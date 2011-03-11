package test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import edu.wustl.mobilab.sliver.bpel.BPELServer;
import edu.wustl.mobilab.sliver.bpel.Binding;
import edu.wustl.mobilab.sliver.bpel.j2se.SocketBinding;
import edu.wustl.mobilab.sliver.soap.Transport;
import edu.wustl.mobilab.sliver.soap.j2se.SocketTransport;
import edu.wustl.mobilab.sliver.util.MalformedDocumentException;

public class BPELServerTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//Oppretter en transport variabel.
		Transport transport = null;
		
		//Instansierer transport med en socket transport og en port!
		try {
			transport = new SocketTransport(22);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Oppretter BPELServeren og legger til transporten (socketen og en port å lytte på)
		BPELServer bpelServer = new BPELServer(transport);
		
		//Prøver å legge til en prosess.
		try {
			bpelServer.addProcess("test", new FileInputStream("SomeProcess.bpel"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedDocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Dokumentasjon trengs
		bpelServer.bindIncomingLink("TestLink", "http://test/namespace", "kjørDenneProsessen");
		
		Binding binding = new SocketBinding("127.0.0.1", 24);
		bpelServer.bindOutgoingLink("EksternSOAPServerTestLink", binding);
		
		//Prøver å starte serveren
		try {
			bpelServer.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
