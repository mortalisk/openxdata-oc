/*
 *  Licensed to the OpenXdata Foundation (OXDF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The OXDF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with the License. 
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, 
 *  software distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the License.
 *
 *  Copyright 2010 http://www.openxdata.org.
 */
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
        Map<String, List<String>> multiSelFields = xform.getMultiSelectFields();

        List<String> header = getHeader(xform, multimediaFields, gpsFields, multiSelFields);
        try {
            writeHeader(out, header);
            for (Object[] line : data) {
                Map<String, String> dataLine = getDataLine(line, multimediaFields, gpsFields, multiSelFields);
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
            }
            line.append(d);
        }
        out.write(line + "\n");
    }

    private Map<String, String> getDataLine(Object[] data, List<String> multimediaFields, List<String> gpsFields, Map<String, List<String>> multiSelFields) throws DOMException {
        Map<String, String> dataLine = new HashMap<String, String>();
        addAuditFields(dataLine, data);
        // other data
        String xml = (String) data[3];
        NodeList nodes = XmlUtil.fromString2Doc(xml).getDocumentElement().getChildNodes();
        int size = nodes.getLength();
        for (int i = 0; i < size; i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            String name = node.getNodeName();
            String value = node.getTextContent();

            // we do not export multimedia field
            if (multimediaFields.contains(name)) {
                continue;
            } else if (gpsFields.contains(node.getNodeName())) {
                addGPSField(dataLine, name, value);
            } else if (multiSelFields.keySet().contains(name)) {
                addMultiSelectField(dataLine, multiSelFields, name, value);
            } else {
                dataLine.put(name.toUpperCase(), formatCSV(value));
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
