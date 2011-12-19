package org.openxdata.server.export;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openxdata.server.admin.model.FormDefVersion;
import org.openxdata.server.admin.model.exception.UnexpectedException;
import org.openxdata.server.service.DataExportService;
import org.openxdata.server.util.XmlUtil;
import org.openxdata.server.xforms.DomReadOnlyXForm;
import org.openxdata.server.xforms.XForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Exports form data to CSV format and writes it to a stream.
 * 
 * @author daniel
 * @author dagmar@cell-life.org.za
 * @author Jonny Heggheim
 * 
 */
@Component("cvsDataExport")
public class CSVDataExport implements DataExport {

    @Autowired
    private DataExportService dataExportService;
    private static SimpleDateFormat dateFormater = new SimpleDateFormat("dd/MM/yyyy");
    ArrayList<Map<String, String>> processedData;
    List<String> header;

    /**
     * Writes data, collected for a particular form version, to a stream in a CSV format.
     * 
     * @param out the stream to write the data.
     * @param formId the unique identifier of the form version whose data we are to export. This should never be null.
     * @param fromDate the data submission date from which start the export. Supply null to include all dates.
     * @param toDate the data submission date to which to end the export. Supply null to include all dates
     * @param userId the user who submitted the data. Supply null to export data for all users.
     */
    @Override
    public void export(Writer out, Integer formId, Date fromDate, Date toDate, Integer userId) {
    	 processedData = new ArrayList<Map<String, String>>();
    	 header = new ArrayList<String>();    	
        // Check to ensure that we have such a form version as identified by the formId.
        FormDefVersion formDefVersion = dataExportService.getFormDefVersion(formId);
        if (formDefVersion == null) {
            return;
        }
        List<Object[]> data = dataExportService.getFormDataWithAuditing(formId, fromDate, toDate, userId);

        // Parse the xform and generate the model
        XForm xform = new DomReadOnlyXForm(formDefVersion.getXform());
        List<String> gpsFields = xform.getGPSFields();
        List<String> multimediaFields = xform.getMultimediaFields();
        List<String> repeatFields = xform.getRepeatFields();
        Map<String, List<String>> multiSelFields = xform.getMultiSelectFields();

        header = getHeader(xform, multimediaFields, gpsFields, multiSelFields);        
        for (Object[] line : data) {
           processedData.add(getDataLine(line, multimediaFields, gpsFields, multiSelFields, repeatFields));            
        }
        try {
            writeHeader(out, header);
            for (Map<String, String> dataLine : processedData) {
                writeDataLine(out, header, dataLine);
            }
        } catch (IOException ex) {
            throw new UnexpectedException(ex);
        }
    }

    private void writeHeader(Writer out, List<String> header) throws IOException {
        out.write(StringUtils.collectionToCommaDelimitedString(header) + "\n");
    }

    private void writeDataLine(Writer out, List<String> header, Map<String, String> dataLine) throws IOException {
        // output in the correct order
        StringBuilder line = new StringBuilder();
        for (String h : header) {
            String d = dataLine.get(h);
            if (line.length() > 0) {
                line.append(",");
                if (d == null) { d = ""; }
            }
            line.append(d);
        }
        out.write(line + "\n");
    }

    private Map<String, String> getDataLine(Object[] data, List<String> multimediaFields, List<String> gpsFields, Map<String, List<String>> multiSelFields, List<String> repeatFields) throws DOMException {
        Map<String, String> dataLine = new HashMap<String, String>();
        addAuditFields(dataLine, data);
        Map<String, Integer> repeatCount = new HashMap<String,Integer>();
        // other data
        String xml = ((String) data[3]).trim();
        NodeList nodes = XmlUtil.fromString2Doc(xml).getDocumentElement().getChildNodes();
        int size = nodes.getLength();
        for (int i = 0; i < size; i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            String name = node.getLocalName();
            String value = node.getTextContent();

            // we do not export multimedia field
            if (multimediaFields.contains(name)) {
                continue;
            } else if (gpsFields.contains(name)) {
                addGPSField(dataLine, name, value);
            } else if (multiSelFields.keySet().contains(name)) {
                addMultiSelectField(dataLine, multiSelFields, name, value);
            } else {
				dataLine = processNode(dataLine, node, repeatCount, repeatFields);
            }
        }
        return dataLine;
    }

	private Map<String, String> processNode(Map<String, String> dataLine, Node node, Map<String, Integer> repeatCount, List<String> repeatNodes) {

		NodeList childNodes = node.getChildNodes();
		int currentRepeat = 1;
		String nodeName = "";	
		
		if (childNodes.getLength() > 1) { // there are child nodes with data that needs processing
			header.remove(node.getLocalName().toUpperCase());				
			for (int i = 0; i < childNodes.getLength(); i++) {
				if (childNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
					dataLine = processNode(dataLine, childNodes.item(i), repeatCount, repeatNodes);
				}
			}
		} else { // no child nodes that need processing
			if (repeatNodes.contains(node.getParentNode().getLocalName())) { // if this column already exists in this dataline						
				if (repeatCount.containsKey(node.getLocalName().toUpperCase())) { // find the number of times this column has been repeated
					currentRepeat = repeatCount.get(node.getLocalName().toUpperCase());
					nodeName = node.getParentNode().getLocalName().toUpperCase() + "_" + node.getLocalName().toUpperCase() + "_" + currentRepeat;
					if (!header.contains(nodeName)) {
						header.add(nodeName); 
						header.remove(node.getLocalName().toUpperCase());
					}					
				} else {
					currentRepeat = 1;
					nodeName = node.getParentNode().getLocalName().toUpperCase() + "_" + node.getLocalName().toUpperCase() + "_" + currentRepeat;
					if (!header.contains(nodeName)) {	
						header.add(header.indexOf(node.getLocalName().toUpperCase()), nodeName);
						header.remove(node.getLocalName().toUpperCase());
					}					
				}
				
				dataLine.put(nodeName, formatCSV(node.getTextContent().trim()));				
				repeatCount.put(node.getLocalName().toUpperCase(), currentRepeat + 1);
			}
			else { // the column does not exist yet
				dataLine.put(node.getLocalName().toUpperCase(), formatCSV(node.getTextContent().trim())); // if this column doesn't exist, we can just add it
			}
		}

		return dataLine;
	}
	
    private List<String> getHeader(XForm xform,
            List<String> multimediaFields,
            List<String> gpsFields,
            Map<String, List<String>> multiSelFields) {

        List<String> header = new ArrayList<String>();
        addAuditingHeader(header);

        Set<String> multiSelectFields = multiSelFields.keySet();
        for (String name : xform.getFieldNames()) {
            // we do not export multimedia field
            if (multimediaFields.contains(name)) {
                continue;
            } else if (gpsFields.contains(name)) {
                addGPSHeader(header, name);
            } else if (multiSelectFields.contains(name)) {
                List<String> options = multiSelFields.get(name);
                addMultiSelectHeader(header, name, options);
            } else {
                header.add(name.toUpperCase());
            }
        }

        return header;
    }

    private void addAuditingHeader(List<String> header) {
        header.add("ID");
        header.add("CAPTURER");
        header.add("CREATION DATE");
    }

    private void addGPSHeader(List<String> header, String name) {
        header.add(name.toUpperCase() + "_LATITUDE");
        header.add(name.toUpperCase() + "_LONGITUDE");
        header.add(name.toUpperCase() + "_ALTITUDE");
    }

    private void addMultiSelectHeader(List<String> header, String name, List<String> options) {
        if (options == null) {
            return;
        }
        for (String option : options) {
            header.add(name.toUpperCase() + "_" + option.toUpperCase());
        }
    }

    /**
     * Formats a value in in the CSV style.
     * 
     * @param value the value for format.
     * @return the CSV formated value.
     */
    private String formatCSV(String value) {
        return value.replaceAll("\"", "\"\"");
    }

    private void addAuditFields(Map<String, String> dataLine, Object[] o) {
        dataLine.put("ID", o[0].toString());
        dataLine.put("CAPTURER", (String) o[1]);
        dataLine.put("CREATION DATE", dateFormater.format((Date) o[2]));
    }

    /**
     *  Split each the GPS coordinates into latitude, longitude and altitude
     *  and add each of them to dataline.
     * 
     */
    private void addGPSField(Map<String, String> dataLine, String name, String value) {
        if (value != null && value.trim().length() > 0) {
            String[] coordinates = value.split(",");
            if (coordinates.length >= 3) {
                dataLine.put(name.toUpperCase() + "_LATITUDE", coordinates[0]);
                dataLine.put(name.toUpperCase() + "_LONGITUDE", coordinates[1]);
                dataLine.put(name.toUpperCase() + "_ALTITUDE", coordinates[2]);
            }
        }
    }

    private void addMultiSelectField(Map<String, String> dataLine, Map<String, List<String>> multiSelFields, String name, String value) {
        List<String> options = multiSelFields.get(name);
        if (options != null) {
            for (String option : options) {
                String selected = (value.contains(option)) ? "1" : "0";
                dataLine.put(name.toUpperCase() + "_" + option.toUpperCase(), selected);
            }
        }
    }

    void setDataExportService(DataExportService dataExportService) {
        this.dataExportService = dataExportService;
    }
}
