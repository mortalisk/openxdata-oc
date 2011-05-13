package org.openxdata.server.export;

import org.openxdata.server.admin.model.FormDefVersion;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.openxdata.server.service.DataExportService;

import static org.mockito.Mockito.*;

/**
 *
 * @author Jonny Heggheim
 */
@RunWith(ConcordionRunner.class)
public class ExportCvsAcceptanceTest {

    private static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private static final int FORM_ID = 1;
    private List<Field> fields;
    private List<String[]> submissions;
    private String capturer;
    private Date captured;
    private DataExportService exportServiceMock;
    private CSVDataExport exporter;

    @Before
    public void setupMocks() {
        exportServiceMock = mock(DataExportService.class);
        exporter = new CSVDataExport();
        exporter.setDataExportService(exportServiceMock);
    }

    @Before
    public void reset() {
        fields = new ArrayList<Field>();
        submissions = new ArrayList<String[]>();
    }

    public void setCapturer(String name) {
        this.capturer = name;
    }

    public void setDate(String date) throws ParseException {
        this.captured = dateFormat.parse(date);
    }

    public void addField(String fieldName) {
        Field field = new Field(fieldName, "string");
        fields.add(field);
    }

    public void addField(String fieldName, String fieldType) {
        Field field = new Field(fieldName, fieldType);
        fields.add(field);
    }

    public void addSubmission(String data) {
        String[] values = data.split("#");
        submissions.add(values);
    }

    public String export() {
        FormDefVersion form = dummyFormDef();

        when(exportServiceMock.getFormDefVersion(FORM_ID)).thenReturn(form);
        when(exportServiceMock.getFormDataWithAuditing(FORM_ID, null, null, null)).thenReturn(generateData());

        StringWriter writer = new StringWriter();
        exporter.export(writer, FORM_ID, null, null, null);
        reset();
        return writer.toString();
    }

    public String export(String xform) {
        FormDefVersion formDef = new FormDefVersion();
        formDef.setXform(xform);

        when(exportServiceMock.getFormDefVersion(FORM_ID)).thenReturn(formDef);
        when(exportServiceMock.getFormDataWithAuditing(FORM_ID, null, null, null)).thenReturn(generateData());

        StringWriter writer = new StringWriter();
        exporter.export(writer, FORM_ID, null, null, null);
        reset();
        return writer.toString();
    }

    private FormDefVersion dummyFormDef() {
        FormDefVersion result = new FormDefVersion();
        result.setXform(generateXForms());
        return result;
    }

    private String generateXForms() {
        String xforms =
                "<xforms>"
                + "  <model>"
                + "    <instance id=\"test1\">"
                + "      <test1 name=\"test form 1\" id=\"1\">";
        for (Field field : fields) {
            xforms += "<" + field.name + "/>";
        }
        xforms += "      </test1>"
                + "    </instance>";

        for (Field field : fields) {
            xforms += "<bind id=\"" + field.name
                    + "\" nodeset=\"/test1/" + field.name
                    + "\" type=\"" + field.type + "\"/>";
        }

        xforms += "  </model>"
                + "</xforms>";

        return xforms;
    }

    private List<Object[]> generateData() {
        List<Object[]> result = new LinkedList<Object[]>();
        int id = 1;
        for (String[] submission : submissions) {
            Object[] values = new Object[4];
            values[0] = new Integer(id);
            values[1] = capturer;
            values[2] = captured;

            String xml = "<test>";
            for (int i = 0; i < fields.size(); i++) {
                xml += "<" + fields.get(i).name + ">" + submission[i] + "</" + fields.get(i).name + ">";
            }
            xml += "</test>";
            values[3] = xml;

            result.add(values);
            id++;
        }

        return result;
    }

    private class Field {

        String name;
        String type;

        public Field(String name, String type) {
            this.name = name;
            this.type = type;
        }
    }
}
