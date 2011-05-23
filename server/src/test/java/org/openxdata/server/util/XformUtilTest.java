package org.openxdata.server.util;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit test for xform to xhtml transformation.
 * 
 * @author daniel
 *
 */
public class XformUtilTest {

    private String xformWithXfPrefix =
            "<xf:xforms xmlns:xf=\"http://www.w3.org/2002/xforms\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"> "
            + " <xf:model> "
            + "   <xf:instance id=\"newform1\"> "
            + "     <newform1 name=\"New Form1\" id=\"1234\"> "
            + "       <question1/> "
            + "       <question2/> "
            + "     </newform1> "
            + "   </xf:instance> "
            + "   <xf:bind id=\"question1\" nodeset=\"/newform1/question1\" type=\"xsd:string\"/> "
            + "   <xf:bind id=\"question2\" nodeset=\"/newform1/question2\" type=\"xsd:string\"/> "
            + " </xf:model> "
            + " <xf:group id=\"1\"> "
            + "   <xf:label>Page1</xf:label> "
            + "  <xf:input bind=\"question1\"> "
            + "    <xf:label>Question1</xf:label> "
            + "  </xf:input> "
            + "  <xf:input bind=\"question2\"> "
            + "    <xf:label>Question2</xf:label> "
            + "  </xf:input> "
            + " </xf:group> "
            + " </xf:xforms>";
    private String xformWithoutPrefix =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<xforms xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">"
            + " <model> "
            + "   <instance id=\"newform1\"> "
            + "     <newform1 name=\"New Form1\" id=\"1234\"> "
            + "       <question1/> "
            + "       <question2/> "
            + "     </newform1> "
            + "   </instance> "
            + "   <bind id=\"question1\" nodeset=\"/newform1/question1\" type=\"xsd:string\"/> "
            + "   <bind id=\"question2\" nodeset=\"/newform1/question2\" type=\"xsd:string\"/> "
            + " </model> "
            + " <group id=\"1\"> "
            + "   <label>Page1</label> "
            + "  <input bind=\"question1\"> "
            + "    <label>Question1</label> "
            + "  </input> "
            + "  <input bind=\"question2\"> "
            + "    <label>Question2</label> "
            + "  </input> "
            + " </group> "
            + " </xforms>";

    @Test
    public void testFromXform2Xhtml() throws Exception {
        String xhtml = XformUtil.fromXform2Xhtml(xformWithXfPrefix);
        assertNotNull(xhtml);
    }

    @Test
    public void addFormIdWithXfNamespacePrefix() {
        String actual = XformUtil.addFormId2Xform(54321, xformWithXfPrefix);
        assertFalse(actual.contains("12345"));
        assertTrue(actual.contains("54321"));
    }

    @Test
    public void addFormIdWithoutNamespacePrefix() {
        String actual = XformUtil.addFormId2Xform(54321, xformWithoutPrefix);
        assertFalse(actual.contains("12345"));
        assertTrue(actual.contains("54321"));
    }
}
