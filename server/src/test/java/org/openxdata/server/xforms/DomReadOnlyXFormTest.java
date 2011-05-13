package org.openxdata.server.xforms;

import java.util.List;
import org.junit.Test;
import org.openxdata.test.XFormsFixture;

import static org.junit.Assert.*;

/**
 *
 * @author Jonny Heggheim
 */
public class DomReadOnlyXFormTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenNullInput() {
        new DomReadOnlyXForm(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenEmptyInput() {
        new DomReadOnlyXForm("");
    }

    @Test(expected = Throwable.class)
    public void shouldThrowExceptionOnNonValidXml() {
        new DomReadOnlyXForm("abc");
    }

    @Test
    public void shouldFindAllFields() {
        DomReadOnlyXForm xForm = new DomReadOnlyXForm(dummyForm);
        List<String> fields = xForm.getFieldNames();
        assertEquals(6, fields.size());
        assertTrue(fields.contains("location1"));
        assertTrue(fields.contains("location2"));
        assertTrue(fields.contains("audio1"));
        assertTrue(fields.contains("video1"));
        assertTrue(fields.contains("picture1"));
        assertTrue(fields.contains("name"));
    }

    @Test
    public void shouldFindAllGpsFields() {
        DomReadOnlyXForm xform = new DomReadOnlyXForm(dummyForm);
        List<String> fields = xform.getGPSFields();
        assertEquals(2, fields.size());
        assertTrue(fields.contains("location1"));
        assertTrue(fields.contains("location2"));
    }

    @Test
    public void shouldFindNoGpsFields() {
        DomReadOnlyXForm xform = new DomReadOnlyXForm(XFormsFixture.getSampleForm());
        List<String> fields = xform.getGPSFields();
        assertEquals(0, fields.size());
    }

    @Test
    public void testGetMultimediaFields() {
        DomReadOnlyXForm xform = new DomReadOnlyXForm(dummyForm);
        List<String> fields = xform.getMultimediaFields();
        assertEquals(3, fields.size());
        assertTrue(fields.contains("audio1"));
        assertTrue(fields.contains("video1"));
        assertTrue(fields.contains("picture1"));
    }

    @Test
    public void shouldFindNoMultimediaFields() {
        DomReadOnlyXForm xform = new DomReadOnlyXForm(XFormsFixture.getExampleXform2());
        List<String> fields = xform.getMultimediaFields();
        assertEquals(0, fields.size());
    }

    @Test
    public void getXFormShouldNotModifyOriginalDocument() {
        DomReadOnlyXForm xform = new DomReadOnlyXForm(dummyForm);
        assertEquals(dummyForm, xform.getXForm());
    }
    String dummyForm =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<xforms xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">"
            + "  <model>"
            + "    <instance id=\"test_test_form2_v1\">"
            + "      <test_test_form2_v1 name=\"Test Form2_v1\" id=\"4\" formKey=\"test_test_form2_v1\">"
            + "        <location1/>"
            + "        <picture1/>"
            + "        <video1/>"
            + "        <location2/>"
            + "        <audio1/>"
            + "        <name/>"
            + "      </test_test_form2_v1>"
            + "    </instance>"
            + "    <bind id=\"location1\" nodeset=\"/test_test_form2_v1/location1\" format=\"gps\" type=\"xsd:string\"/>"
            + "    <bind id=\"picture1\" nodeset=\"/test_test_form2_v1/picture1\" format=\"image\" type=\"xsd:base64Binary\"/>"
            + "    <bind id=\"video1\" nodeset=\"/test_test_form2_v1/video1\" type=\"xsd:base64Binary\" format=\"video\"/>"
            + "    <bind id=\"location2\" nodeset=\"/test_test_form2_v1/location2\" format=\"gps\" type=\"xsd:string\"/>"
            + "    <bind id=\"audio1\" nodeset=\"/test_test_form2_v1/audio1\" format=\"audio\" type=\"xsd:base64Binary\"/>"
            + "    <bind id=\"name\" nodeset=\"/test_test_form2_v1/name\" type=\"xsd:string\"/>"
            + "  </model>"
            + "  <group id=\"1\">"
            + "    <label>Audio1</label>"
            + "    <input bind=\"location1\">"
            + "      <label>Location1</label>"
            + "    </input>"
            + "    <upload bind=\"picture1\" mediatype=\"image/*\">"
            + "      <label>Picture1</label>"
            + "    </upload>"
            + "    <upload bind=\"video1\" mediatype=\"video/*\">"
            + "      <label>Video1</label>"
            + "    </upload>"
            + "    <input bind=\"location2\">"
            + "      <label>Location2</label>"
            + "    </input>"
            + "    <upload bind=\"audio1\" mediatype=\"audio/*\">"
            + "      <label>Audio1</label>"
            + "    </upload>"
            + "    <input bind=\"name\">"
            + "      <label>Name</label>"
            + "    </input>"
            + "  </group>"
            + "</xforms>";
}
