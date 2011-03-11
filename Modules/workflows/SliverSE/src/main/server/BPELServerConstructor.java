package main.server;

import java.io.FileInputStream;

import edu.wustl.mobilab.sliver.bpel.BPELServer;
import edu.wustl.mobilab.sliver.bpel.Binding;
import edu.wustl.mobilab.sliver.bpel.j2se.SocketBinding;
import edu.wustl.mobilab.sliver.soap.Transport;
import edu.wustl.mobilab.sliver.soap.j2se.SocketTransport;

public class BPELServerConstructor {

	private int soapListenPort;
	/**
	 * NameSpace example: "http://jbpm.org/examples/hello"
	 */
	private String nameSpace;
	private String remoteIP;
	private int remotePort;
	private String bpelFile;
	
	
	public BPELServerConstructor(int soapListenPort, String nameSpace, String remoteIP, int remotePort, String bpelFile){
		this.soapListenPort = soapListenPort;
		this.nameSpace = nameSpace;
		this.remoteIP = remoteIP;
		this.remotePort = remotePort;
		this.bpelFile = bpelFile;
	}
	
	public void startServer() throws Exception{
		String namespace = nameSpace;

		//Opens a socket on a port that listens for incoming connections.
        Transport transport = new SocketTransport(soapListenPort);
        
        //The transport is given to the BPEL constructor, that passes it on to the SOAPServer constructor. Its added there.
        BPELServer server = new BPELServer(transport);
        server.addProcess(namespace, new FileInputStream(bpelFile));

        server.bindIncomingLink("caller", namespace, "sayHello");

        Binding remoteHost = new SocketBinding(remoteIP, remotePort);
        server.bindOutgoingLink("ExternalSOAPServiceLink", remoteHost);
        
        //Calls the start method in SOAPServer (that BPEL inherits) which will loop thru all transports and open them, 
        //they will then start listening for incoming connections.
        server.start();
	}
}
