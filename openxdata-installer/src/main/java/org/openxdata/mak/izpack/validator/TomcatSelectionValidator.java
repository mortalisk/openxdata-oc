package org.openxdata.mak.izpack.validator;

import com.izforge.izpack.installer.AutomatedInstallData;
import com.izforge.izpack.installer.DataValidator;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author kay
 */
//C:\DEV\frameworks\apache-tomcat-6.0.20
public class TomcatSelectionValidator implements DataValidator {

        private String error = new String();

        @Override
        public Status validateData(AutomatedInstallData aid) {
                System.out.println("Validating Tomcat");
                String tomcatDir = aid.getVariable("tomcat.dir.user");
                File serverXML = new File(tomcatDir + "\\conf\\server.xml");
                int port = Integer.parseInt(aid.getVariable("tomcat.port"));
                try {
                        Document serverXml = fromFileToDoc(serverXML);
                        port = readPortNumberFromDoc(serverXml);
                        aid.setVariable("tomcat.port", port + "");

                } catch (Exception ex) {
                        error = "Failed to read your Tomcat server port going to use: " + port + " \n" + ex.getMessage();
                        return Status.WARNING;
                }
                return Status.OK;
        }

        @Override
        public String getErrorMessageId() {
                return error;
        }

        @Override
        public String getWarningMessageId() {
                return error;
        }

        @Override
        public boolean getDefaultAnswer() {
                return true;
        }

        public int readPortNumberFromDoc(Document doc) throws ParserConfigurationException, SAXException, IOException {
                NodeList connectorNodes = doc.getElementsByTagName("Connector");
                int port = 0;
                for (int i = 0; i < connectorNodes.getLength(); i++) {
                        Node item = connectorNodes.item(i);
                        if (isHTTPNode(item))
                                port = Integer.parseInt(item.getAttributes().getNamedItem("port").getNodeValue());
                }
                return port;
        }

        private static boolean isHTTPNode(Node item) {
                NamedNodeMap attributes = item.getAttributes();
                if (attributes != null) {
                        Node namedItem = attributes.getNamedItem("protocol");
                        if (namedItem != null)
                                return namedItem.getNodeValue().startsWith("HTTP");
                }
                return false;
        }

        private Document fromFileToDoc(File xmlFile) throws ParserConfigurationException, SAXException, IOException {
                DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
                return docBuilder.parse(xmlFile);
        }
}
