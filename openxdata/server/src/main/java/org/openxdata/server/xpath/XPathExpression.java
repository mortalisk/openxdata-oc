package org.openxdata.server.xpath;

import java.io.Serializable;
import java.util.Vector;

import org.w3c.dom.Node;

/**
 * @author Cosmin
 * @author daniel
 */
public class XPathExpression implements Serializable {

    private static final long serialVersionUID = 1L;
    String[] locationStepStringsArray;
    XPathLocationStep[] locationStepArray;
    Vector<Node> resultNodeSet;
    String expression = null;
    Node startNode = null;

    @SuppressWarnings("unchecked")
    public XPathExpression(Node startNode, String expression) {
        Vector<String> tmp = new Vector<String>();

        this.startNode = startNode;
        this.expression = expression;

        //I do not support function name in the start
        //of an xpath expression

        //parse
        if (expression.startsWith("//")) {
            //this way of handling "//" is obviously incomplete
            //but we allow it like this because of the lacking resources
            tmp.addElement("//");
            expression = new String(expression.toCharArray(), 2, expression.length() - 2);
        } else if (expression.startsWith("/")) {
            tmp.addElement("/");
            //trace the root element
            expression = new String(expression.toCharArray(), 1, expression.length() - 1);
        }

        for (int start = 0, end = 0; end < expression.length() - 1 && end != -1; start = end + 1) {
            end = expression.indexOf("/", start);

            if (end != -1) {
                String token = expression.substring(start, end);
                if (token.indexOf('@') >= 0 && token.indexOf(']') < 0) {
                    end = expression.indexOf("]", end + 1) + 1;
                }
            }

            String s = new String(expression.toCharArray(), start,
                    (end != -1 ? end : expression.length()) - start);

            if (s.indexOf('@') > 0) {
                addAttributeSteps(s, tmp);
            } else {
                tmp.addElement(s);
            }
        }
        locationStepStringsArray = new String[tmp.size()];
        tmp.copyInto(locationStepStringsArray);
        tmp = null;

        //the result node set should contain nodes
        //with regard to the starting poing of the xpath expression
        //for now just pass the root of the document
        resultNodeSet = new Vector<Node>();
        resultNodeSet.addElement(startNode);

        boolean attributeFound = false;
        Vector<Node> prevResults = null;

        //start processing every location
        for (int j = 0; j < locationStepStringsArray.length; j++) {
            prevResults = new Vector<Node>();

            String locationStepString = locationStepStringsArray[j];
            if (locationStepString.indexOf('@') >= 0) {
                if (attributeFound) {
                    prevResults = resultNodeSet;
                }
                attributeFound = true;
            } else {
                attributeFound = false;
            }

            XPathLocationStep locationStep = new XPathLocationStep(locationStepString);

            resultNodeSet = locationStep.getResult(resultNodeSet, prevResults);
        }
    }

    private void addAttributeSteps(String step, Vector<String> list) {
        int posBeg = 0;
        int posEnd = step.indexOf(" and ");

        posEnd = step.indexOf(']', posBeg);

        while (posEnd > 0) {
            list.addElement(step.substring(posBeg, posEnd + 1));
            posBeg = posEnd + 1;
            if (posBeg >= step.length()) {
                break;
            }
            posEnd = step.indexOf(']', posBeg);
        }
    }

    public Vector<Node> getResult() {
        return resultNodeSet;
    }
}
