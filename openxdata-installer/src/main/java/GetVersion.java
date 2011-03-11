
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;

public class GetVersion
{
    public static void main(String argv[]) throws ParserConfigurationException, SAXException, IOException, Exception
    {
        Document doc = getWebXMLDocument(argv[0]);
        NodeList listOfDisplayNames = doc.getElementsByTagName("display-name");
        int totalNodes = listOfDisplayNames.getLength();
        if (totalNodes == 0)
            return;
        String version = null;
        if (totalNodes > 1) {//Old openxdata war file
            version = getVersionFromNodes(totalNodes, listOfDisplayNames);
        } else {
            version = listOfDisplayNames.item(0).getChildNodes().
                    item(0).
                    getNodeValue().
                    replace("@", "").replaceAll("(?i)server", "").replaceAll("(?i)openxdata", "").trim().replaceAll("\\s+", ".");
        }
        System.out.println("Version In XML: " + version);
        writeVersionToFile(argv[1], version);
    }

    public static Document getWebXMLDocument(String webXml) throws ParserConfigurationException, IOException, SAXException
    {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        File webXML = new File(webXml);
        System.out.println("Parsing XML : " + webXML.getAbsolutePath());
        Document doc = docBuilder.parse(webXML);
        doc.getDocumentElement().normalize();
        return doc;
    }

    private static String getVersionFromNodes(int totalNodes, NodeList listOfDisplayNames) throws DOMException
    {
        String version = null;
        for (int i = 0; i < totalNodes; i++) {
            Node item = listOfDisplayNames.item(i);
            NodeList node = item.getChildNodes();
            Node item1 = node.item(0);
            String nodeValue = item1.getNodeValue();
            if ((version = findVersionInString(nodeValue)) != null) {
                break;
            }
        }
        return version;
    }

    private static String findVersionInString(String str)
    {
        String veS = null;
        Pattern p = Pattern.compile("\\d*[.]\\d*[.]\\d*");
        Matcher m = p.matcher(str);
        if (m.find())
            veS = str.substring(m.start(), m.end());
        return veS;
    }

    private static void writeVersionToFile(String filename, String version) throws Exception
    {
        File f = new File(filename);
        if (!f.exists())
            f.createNewFile();
        FileReader fr = new FileReader(f);
        Properties p = new Properties();
        p.load(fr);
        p.setProperty("versionname", version);
        p.setProperty("version", getRevisionFromString(version));
        //p.setProperty("build.date", new Date());
        p.list(System.out);
        FileOutputStream fout = new FileOutputStream(f);
        p.store(fout, "Version Info");
        fr.close();
        fout.close();
    }

    private static String getRevisionFromString(String revision)
    {
        revision = revision.trim();
        try {
            Integer.parseInt(revision.replace(".", ""));
            return revision;
        } catch (Exception e) {
        }
        String majorVersion = revision.substring(0, 3);
        int lastIndexOf = revision.lastIndexOf('r');
        String revNumber = revision.substring(lastIndexOf + 1).trim();
        String version = majorVersion + "." + revNumber;
        return version;
    }
}
