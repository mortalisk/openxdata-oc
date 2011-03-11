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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang.StringUtils;

import org.openxdata.server.admin.model.exception.UnexpectedException;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import static org.openxdata.server.export.rdbms.engine.Constants.*;

/**
 * Class that builds the table structures in which the exported data will be inserted
 * @author Tumwebaze Charles
 *
 */
public class StructureBuilder {
    private final String LINE_SEPARATOR = System.getProperty("line.separator");
    
    @SuppressWarnings("unchecked")
	public List<TableQuery> createStructure(Document schemaDocument) {
        List<TableQuery> tables = new ArrayList<TableQuery>();
        if (schemaDocument == null) {
            throw new UnexpectedException("schemaDocument cannot be null");
        }

        NodeList instanceNodes = schemaDocument.getElementsByTagNameNS("*", "instance");

        //for now we shall be processing only one instance
        if (instanceNodes.getLength() == 0) {
            return Collections.EMPTY_LIST;
        }
        
        Node instanceNode = instanceNodes.item(0);
        //clean the node
        Functions.cleanNode(instanceNode);

        NodeList tableElements = instanceNode.getChildNodes();
        for (int i = 0; i < tableElements.getLength(); i++) {
            Node tableElement = tableElements.item(i);
            if (Functions.isValidNode(tableElement)) {
                createTable(schemaDocument, tableElement, null, tables);

                //break out after processing one child of the instance node
                break;
            }
        }
        return tables;
    }
	
    private String escape(String what) {
        return ESCAPE_CHAR + what + ESCAPE_CHAR;
    }

    private void createTable(Document schemaDocument, Node tableElement, String parentTable, List<TableQuery> tables) {
        if (!Functions.hasValidChildNodes(tableElement)) {
            return;
        }

        Functions.cleanNode(tableElement);
        String tableName = tableElement.getNodeName();
        if (StringUtils.isBlank(tableName)) {
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        appendCreateTable(sb, tableName);
        appendIdColumn(sb);
        appendFormDataId(sb);
        appendUserId(sb);
        appendUserName(sb);

        //creating other columns
        NodeList columnElements = tableElement.getChildNodes();
        for (int index = 0; index < columnElements.getLength(); index++) {
            Node columnElement = columnElements.item(index);
            if (Functions.isValidNode(columnElement)) {
                Functions.cleanNode(columnElement);

                //if the column element has children, recurse so that we create a table
                if (Functions.hasValidChildNodes(columnElement)) {
                    //if the column element is a repeat
                    if (Functions.isRepeat(schemaDocument, columnElement)) {
                        createTable(schemaDocument, columnElement, tableName, tables);
                        continue;
                    } else {
                        NodeList children = columnElement.getChildNodes();
                        for (int child = 0; child < children.getLength(); child++) {
                            Node childElement = children.item(child);
                            if (Functions.isRepeat(schemaDocument, childElement)) {
                                createTable(schemaDocument, childElement, tableName, tables);
                                continue;
                            } else {
                                if (!(childElement instanceof Text || childElement instanceof Comment)) {
                                    createColumnStructure(schemaDocument, sb, childElement);
                                }
                            }
                        }
                        continue;
                    }
                }

                createColumnStructure(schemaDocument, sb, columnElement);
            }
        }

        //create the parent column
        if (StringUtils.isNotBlank(parentTable)) {
            appendParentColumn(sb, parentTable);
        }

        sb.append(CLOSING_BRACE);
        sb.append("TYPE = INNODB;");

        tables.add(0, new TableQuery(tableName, sb.toString()));
    }

    private void appendCreateTable(StringBuilder sb, String tableName) {
        sb.append(String.format(SQL_CREATE_TABLE, escape(tableName)));
        sb.append(LINE_SEPARATOR);
        sb.append(OPENING_BRACE);
        sb.append(LINE_SEPARATOR);
    }

    private void appendIdColumn(StringBuilder sb) {
        sb.append("\t" + "Id" + " " + TYPE_VARCHAR);
        sb.append(String.format(SIZE_OF_TYPE, 200));
        sb.append(" PRIMARY KEY");
        sb.append(LINE_SEPARATOR);
    }
	
    private void appendFormDataId(StringBuilder sb) {
        sb.append("\t" + ",openxdata_form_data_id" + " " + TYPE_VARCHAR);
        sb.append(String.format(SIZE_OF_TYPE, 50));
        sb.append(LINE_SEPARATOR);
    }

    private void appendUserId(StringBuilder sb) {
        sb.append("\t" + ",openxdata_user_id" + " " + TYPE_VARCHAR);
        sb.append(String.format(SIZE_OF_TYPE, 50));
        sb.append(LINE_SEPARATOR);
    }

    private void appendUserName(StringBuilder sb) {
        sb.append("\t" + ",openxdata_user_name" + " " + TYPE_VARCHAR);
        sb.append(String.format(SIZE_OF_TYPE, 50));
        sb.append(LINE_SEPARATOR);
    }

    private void appendParentColumn(StringBuilder sb, String parentTable) {
        sb.append("\t" + ",ParentId" + " " + TYPE_VARCHAR);
        sb.append(String.format(SIZE_OF_TYPE, 200));
        sb.append(" REFERENCES " + parentTable + "(Id)");
        sb.append(LINE_SEPARATOR);
    }

	/**
	 * 
	 * @param schemaDocument
	 * @param builder
	 * @param columnNode
	 */
	private void createColumnStructure(Document schemaDocument,StringBuilder builder,Node columnNode){
		String columnName = columnNode.getNodeName();
		String columnType = Functions.resolveType(schemaDocument, columnNode);
		
		//create the column SQL statements.
		builder.append("\t ," + escape(columnName) +" "+columnType);
		if(columnType.equalsIgnoreCase(TYPE_INTEGER) || 
				columnType.equalsIgnoreCase(TYPE_DATE) || 
				columnType.equalsIgnoreCase(TYPE_BOOLEAN) ||
				columnType.equalsIgnoreCase(TYPE_DATETIME) || 
				columnType.equalsIgnoreCase(TYPE_TIME) ||
				columnType.equalsIgnoreCase(TYPE_BINARY)) {
			//do nothing for these types
		}
		else
			builder.append(getTypeSizeResolver(columnType));
		
		builder.append(LINE_SEPARATOR);
	}
	
	/**
	 * resolves returns RDBMS type size
	 */
	private String getTypeSizeResolver(String type){
		if (type.equalsIgnoreCase(TYPE_BOOLEAN)) {
			return String.format(SIZE_OF_TYPE, 1);
		} else if(type.equalsIgnoreCase(TYPE_CHAR)) {
			return String.format(SIZE_OF_TYPE, 1);
		} else if(type.equalsIgnoreCase(TYPE_DECIMAL)) {
			return String.format(SIZE_OF_DECIMAL_TYPE, 18, 8);
		} else if(type.equalsIgnoreCase(TYPE_VARCHAR)) {
			return String.format(SIZE_OF_TYPE, 255);
		} else {
			return String.format(SIZE_OF_TYPE, 4);
		}
	}
}
