package org.openxdata.server.xforms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.openxdata.server.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Jonny Heggheim
 */
public class DomReadOnlyXForm implements XForm {

    private final String xform;
    private final Document document;

    public DomReadOnlyXForm(String xform) {
        Validate.notEmpty(xform);

        this.xform = xform;
        this.document = XmlUtil.fromString2Doc(xform);
    }

    public List<String> getFieldNames() {
        List<String> fields = new ArrayList<String>();
        NodeList nodes = document.getElementsByTagNameNS("*", "bind");
        int size = nodes.getLength();
        for (int i = 0; i < size; i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            String nodeset = ((Element) node).getAttribute("nodeset");
            String name = nodeset.substring(nodeset.lastIndexOf('/') + 1);
            fields.add(name);
        }

        return fields;
    }

    public List<String> getGPSFields() {
        List<String> fields = new ArrayList<String>();

        NodeList nodes = document.getElementsByTagNameNS("*", "bind");
        int size = nodes.getLength();
        for (int i = 0; i < size; i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            Element element = (Element) node;
            String type = element.getAttribute("type");
            if (type == null || type.trim().length() == 0) {
                continue;
            }

            String nodeset = element.getAttribute("nodeset");
            String name = nodeset.substring(nodeset.lastIndexOf('/') + 1);
            String format = element.getAttribute("format");

            if ("gps".equals(format)) {
                fields.add(name);
            }
        }

        return fields;
    }

    public List<String> getMultimediaFields() {
        List<String> fields = new ArrayList<String>();

        NodeList nodes = document.getElementsByTagNameNS("*", "bind");
        int size = nodes.getLength();
        for (int i = 0; i < size; i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            Element element = (Element) node;
            String type = element.getAttribute("type");
            if (type == null || type.trim().length() == 0) {
                continue;
            }

            String nodeset = element.getAttribute("nodeset");
            String name = nodeset.substring(nodeset.lastIndexOf('/') + 1);

            if (type.contains("base64Binary")) {
                fields.add(name);
            }
        }

        return fields;
    }

    public Map<String, List<String>> getMultiSelectFields() {
        Map<String, List<String>> fields = new HashMap<String, List<String>>();

        NodeList nodes = document.getElementsByTagNameNS("*", "select");
        int size = nodes.getLength();
        for (int i = 0; i < size; i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Element element = (Element) node;
            String name = element.getAttribute("bind");
            if (StringUtils.isBlank(name)) {
                continue;
            }

            fields.put(name, getSelectOptions(element));
        }

        return fields;
    }

    private List<String> getSelectOptions(Element selectNode) {
        List<String> options = new ArrayList<String>();

        NodeList nodes = selectNode.getElementsByTagNameNS("*", "item");
        int size = nodes.getLength();
        for (int i = 0; i < size; i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            String bind = ((Element) node).getAttribute("id");
            if (bind == null || bind.trim().length() == 0) {
                continue;
            }

            options.add(bind);
        }

        return options;
    }

    public String getXForm() {
        return xform;
    }
}
