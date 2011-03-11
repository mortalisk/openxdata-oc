package main.old;

import java.io.FileInputStream;

import edu.wustl.mobilab.sliver.bpel.BPELServer;
import edu.wustl.mobilab.sliver.bpel.Binding;
import edu.wustl.mobilab.sliver.bpel.j2se.SocketBinding;
import edu.wustl.mobilab.sliver.soap.Transport;
import edu.wustl.mobilab.sliver.soap.j2se.SocketTransport;

public class BPELServerExample
{

    public static void main(String[] args) throws Exception
    {
        String namespace = "http://jbpm.org/examples/hello";

        Transport transport = new SocketTransport(9001);
        BPELServer server = new BPELServer(transport);
        server.addProcess(namespace, new //FileInputStream("ExampleWorkflow.bpel"));
                FileInputStream("hello.bpel"));

        server.bindIncomingLink("caller", namespace,
                "sayHello");

        Binding remoteHost = new SocketBinding("127.0.0.1", 9000);
        server.bindOutgoingLink("ExternalSOAPServiceLink", remoteHost);

        server.start();
    }
}
