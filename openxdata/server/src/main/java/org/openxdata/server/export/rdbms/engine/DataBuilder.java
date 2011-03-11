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
package org.openxdata.server.export.rdbms.engine;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.export.rdbms.task.RdmsDataExportTask;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static org.openxdata.server.export.rdbms.engine.Constants.*;

/**
 * Class responsible for building SQL insert statements for inserting data in the RDBMS tables
 * @author Tumwebaze Charles
 * @author dagmar@cell-life.org.za
 *
 */
public class DataBuilder {

    private Logger log = Logger.getLogger(this.getClass());
    private static final String SQL_INSERT_INTO = "INSERT INTO %s (%s) VALUES (%s);";
    private final Document schemaDocument;
    private final Document dataDocument;

    public DataBuilder(Document schemaDocument, Document dataDocument) {
        this.schemaDocument = schemaDocument;
        this.dataDocument = dataDocument;
        
        Validate.notNull(schemaDocument, "schemaDocument cannot be null");
        Validate.notNull(dataDocument, "dataDocument cannot be null");
    }
    
    public List<DataQuery> buildData(FormData formData, boolean update) {
        List<DataQuery> statements = new ArrayList<DataQuery>();
        NodeList nodes = dataDocument.getChildNodes();
        for (int index = 0; index < nodes.getLength();) {
            Node tableElement = nodes.item(index);
            if (Functions.isValidNode(tableElement)) {
                Functions.cleanNode(tableElement);
                createStatements(update, tableElement, formData, statements, null);
            }

            //for now we process the first child node in document
            break;
        }
        return statements;
    }
	
    private void createStatements(boolean update, Node tableElement, FormData formData,
            List<DataQuery> statements, String parentId) {

        Map<String, Object> columnValues = new HashMap<String, Object>();
        String tableName = tableElement.getNodeName();

        if (!Functions.hasValidChildNodes(tableElement)) {
            return;
        }
        if (StringUtils.isBlank(tableName)) {
            return;
        }

        // initialise generic columns
        String id = tableName;
        if (!update) {
            id = UUID.randomUUID().toString();
            columnValues.put("Id", id);
            columnValues.put("openxdata_form_data_id", String.valueOf(formData.getFormDataId()));
        }
        columnValues.put("openxdata_user_name", formData.getCreator().getName());
        columnValues.put("openxdata_user_id", String.valueOf(formData.getCreator().getUserId()));

        // initialise the columns specific to this form data (or form data child)
        NodeList columnElements = tableElement.getChildNodes();
        for (int index = 0; index < columnElements.getLength(); index++) {
            Node columnElement = columnElements.item(index);
            if (Functions.isValidNode(columnElement)) {
                Functions.cleanNode(columnElement);
                if (Functions.isRepeat(schemaDocument, columnElement)) {
                    // if the column element is a repeat
                    log.debug(tableName + ">column repeat '" + columnElement.getNodeName() + "'");
                    createStatements(update, columnElement, formData, statements, id);
                } else if (Functions.hasValidChildNodes(columnElement)) {
                    // if there are child nodes, go through them
                    log.debug(tableName + ">column with children " + columnElement.getNodeName());
                    NodeList children = columnElement.getChildNodes();
                    for (int child = 0; child < children.getLength(); child++) {
                        Node childElement = children.item(child);
                        if (Functions.isRepeat(schemaDocument, childElement)) {
                            // if the column child element is a repeat
                            log.debug(tableName + ">child repeat '" + childElement.getNodeName() + "'");
                            createStatements(update, childElement, formData, statements, id);
                        } else if (Functions.isValidNode(childElement)) {
                            log.debug(tableName + ">child ordinary " + childElement.getNodeName());
                            Object columnValue = getColumnValue(schemaDocument, childElement);
                            columnValues.put(childElement.getNodeName(), columnValue);
                        }
                    }
                } else {
                    // ordinary column with no child nodes
                    log.debug(tableName + ">ordinary column " + columnElement.getNodeName());
                    Object columnValue = getColumnValue(schemaDocument, columnElement);
                    columnValues.put(columnElement.getNodeName(), columnValue);
                }
            }
        }

        // now create the statement (either update or insert) and insert into the list
        DataQuery stmt = null;
        if (update) {
            stmt = createUpdateStatement(tableName, formData.getFormDataId(), parentId, columnValues);
        } else {
            stmt = createInsertStatement(tableName, parentId, columnValues);
        }
        if (log.isDebugEnabled()) {
            log.debug(tableName + ">sql=" + stmt.getSql() + " parameters=" + stmt.getParameters());
        }
        statements.add(0, stmt);
    }
   
   private String escape(String column) {
       return ESCAPE_CHAR + column + ESCAPE_CHAR;
   }

   private DataQuery createInsertStatement(String tableName, String parentId, Map<String, Object> columns) {
       StringBuilder structure = new StringBuilder();
       StringBuilder values = new StringBuilder();
       List<Object> columnValues = new ArrayList<Object>();
       for (String colName : columns.keySet()) {
           if (structure.length() > 0) {
               structure.append(",");
               values.append(",");
           }
           structure.append(escape(colName));
           values.append("?");
           columnValues.add(columns.get(colName));
       }
       if (StringUtils.isNotBlank(parentId)) {
           structure.append(",ParentId");
           values.append(",?");
           columnValues.add(parentId);
       }
       return new DataQuery(SQL_INSERT_INTO, tableName, structure.toString(), values.toString(), columnValues, ESCAPE_CHAR);
   }
   
   private DataQuery createUpdateStatement(String tableName, Integer id, String parentTableName, Map<String, Object> columns) {
       // generate set part of the sql statement
       List<Object> columnValues = new ArrayList<Object>();
       StringBuilder set = new StringBuilder();
       for (String colName : columns.keySet()) {
           if (set.length() > 0) {
               set.append(",");
           }
           set.append(escape(colName));
           set.append("=?");
           columnValues.add(columns.get(colName));
       }
       
       // generate where part of the sql statement
       StringBuilder where = new StringBuilder();
       where.append("openxdata_form_data_id=?");
       columnValues.add(id.toString());
       
       if (StringUtils.isNotBlank(parentTableName)) {
           where.append(" AND parentId = (select Id from "+parentTableName+" where openxdata_form_data_id=?)");
           columnValues.add(id.toString());
       }
       
       return new DataQuery(SQL_UPDATE, tableName, set.toString(), where.toString(), columnValues, ESCAPE_CHAR);
   }
   
    Object getColumnValue(Document schemaDocument, Node node) {
        String value = node.getTextContent();
        if (StringUtils.isBlank(value)) {
            return null;
        }
        String columnType = Functions.resolveType(schemaDocument, node.getNodeName());
        if (columnType.equalsIgnoreCase(TYPE_DATE)) {
            return getDateValue(value, node);
        } else if (columnType.equalsIgnoreCase(TYPE_TIME)) {
            return getTimeValue(value, node);
        } else if (columnType.equalsIgnoreCase(TYPE_DATETIME)) {
            return getDateTimeValue(value, node);
        } else if (columnType.equalsIgnoreCase(TYPE_BOOLEAN)) {
            return new Boolean(value); // note: see Functions.resolveType - boolean is actually VARCHAR
        } else if (columnType.equalsIgnoreCase(TYPE_DECIMAL)) {
            return new Double(value);
        } else if (columnType.equalsIgnoreCase(TYPE_INTEGER)) {
            return new Integer(value);
        } else if (columnType.equalsIgnoreCase(TYPE_BINARY)) {
            return getBinaryValue(node);
        }
        // VARCHAR or CHAR
        return value;
    }

    private byte[] getBinaryValue(Node node) throws DOMException {
        node.normalize();
        String base64 = node.getTextContent();
        byte decoded[] = Base64.decodeBase64(base64.getBytes());
        return decoded;
    }

    private Timestamp getDateTimeValue(String dateTime, Node node) {
        try {
            long datetimeLong = new SimpleDateFormat(RdmsDataExportTask.getSubmitDateTimeSetting()).parse(dateTime).getTime();
            return new Timestamp(datetimeLong);
        } catch (ParseException e) {
            log.warn("Could not convert datetime '" + dateTime + "' using format '" + RdmsDataExportTask.getSubmitDateTimeSetting() + "' for " + node.getNodeName(), e);
        }
        return null;
    }

    private Time getTimeValue(String time, Node node) {
        String xTime = time.toUpperCase();
        try {
            if (xTime.contains("PM") || xTime.contains("AM")) {
                return new Time(new SimpleDateFormat("hh:mm:ss aaa").parse(xTime).getTime());
            } else {
                return Time.valueOf(time); // must be HH:mm:ss format
            }
        } catch (ParseException e) {
            log.warn("Could not convert time '" + time + "' to sql Time for " + node.getNodeName(), e);
        }
        return null;
    }

    private Date getDateValue(String date, Node node) {
        try {
            long dateLong = new SimpleDateFormat(RdmsDataExportTask.getSubmitDateSetting()).parse(date).getTime();
            return new Date(dateLong);
        } catch (ParseException e) {
            log.warn("Could not convert date '" + date + "' using format '" + RdmsDataExportTask.getSubmitDateSetting() + "' for " + node.getNodeName(), e);
        }
        return null;
    }
 
}