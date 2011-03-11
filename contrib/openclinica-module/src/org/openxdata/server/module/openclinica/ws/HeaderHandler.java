package org.openxdata.server.module.openclinica.ws;

import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class HeaderHandler implements SOAPHandler<SOAPMessageContext> {

	//TODO These should be made thread local.
	public static String username = "root";
	public static String password = "f700a6934e78cd908cb5665cd84f89318bfa2d43";
	
	/** The soap response message */
	public static SOAPMessage responseMessage;
	
	public static Document doc;
	
	
	public boolean handleMessage(SOAPMessageContext smc) {

		Boolean outboundProperty = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		if (outboundProperty.booleanValue()) {

			try {
				SOAPEnvelope envelope = smc.getMessage().getSOAPPart().getEnvelope();
				SOAPHeader header = envelope.addHeader();

				SOAPElement security =
					header.addChildElement("Security", "wsse", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");

				SOAPElement usernameToken =
					security.addChildElement("UsernameToken", "wsse");
				usernameToken.addAttribute(new QName("xmlns:wsu"), "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");

				SOAPElement username =
					usernameToken.addChildElement("Username", "wsse");
				username.addTextNode(HeaderHandler.username);

				SOAPElement password =
					usernameToken.addChildElement("Password", "wsse");
				password.setAttribute("Type", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText");
				password.addTextNode(HeaderHandler.password);
				
				if(doc != null){
					Node odmNode = smc.getMessage().getSOAPBody().getOwnerDocument().importNode(doc.getDocumentElement(), true);
					NodeList nodes = smc.getMessage().getSOAPBody().getChildNodes();
					for(int index = 0; index < nodes.getLength(); index++){
						Node node = nodes.item(index);
						if(node.getNodeType() == Node.ELEMENT_NODE){
							node.appendChild(odmNode);
							break;
						}
					}
				}
				
				//Print out the outbound SOAP message to System.out
				//message.writeTo(System.out);
				//System.out.println("");

			} catch (Exception e) {
				e.printStackTrace();
			}

		} 
		else {
			try {

				//This handler does nothing with the response from the Web Service so
				//we just print out the SOAP message.
				responseMessage = smc.getMessage();
				//message.writeTo(System.out);
				//System.out.println("");

			} 
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return outboundProperty;
	}

	public Set getHeaders() {
		//throw new UnsupportedOperationException("Not supported yet.");
		return null;
	}

	public boolean handleFault(SOAPMessageContext context) {
		//throw new UnsupportedOperationException("Not supported yet.");
		return true;
	}

	public void close(MessageContext context) {
		//throw new UnsupportedOperationException("Not supported yet.");
	}
}
