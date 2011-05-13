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
package org.openxdata.server.database.liquibase.customchanges;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import liquibase.FileOpener;
import liquibase.change.custom.CustomSqlChange;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.sql.SqlStatement;
import liquibase.exception.CustomChangeException;
import liquibase.exception.InvalidChangeDefinitionException;
import liquibase.exception.SetupException;
import liquibase.exception.UnsupportedChangeException;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;


public class FixBinaryColumnsExportedData implements CustomSqlChange {
    
    private Logger log = Logger.getLogger(this.getClass());

    @Override
	public SqlStatement[] generateStatements(Database db) throws UnsupportedChangeException, CustomChangeException {
        DatabaseConnection c = db.getConnection();
        
        try {
            // finds all the forms with binary data
            PreparedStatement formDefinitionVersionStatement = c.prepareStatement(
            		"SELECT fdv.form_definition_version_id, fdv.xform " +
            		"FROM form_definition_version fdv " +
            		"WHERE fdv.xform like '%xsd:base64binary%'");
            
            // alter table columns to LONGBLOB
            String alterExportedFormTable = "ALTER table %s MODIFY COLUMN %s LONGBLOB;";
            
            // finds all form_data inserted for form that has already been exported
            PreparedStatement formDataStatement = c.prepareStatement(
            		"SELECT fd.form_data_id, fd.data FROM form_data fd where fd.form_definition_version_id=? and fd.exported&1=1");
            
            String alterExportedBinaryData = "UPDATE %s SET %s=? WHERE openxdata_form_data_id=?";
            
            try {
            	// 1. find forms that need fixing
                ResultSet formDefinitionRS = formDefinitionVersionStatement.executeQuery();
                while (formDefinitionRS.next()) {
                    Integer formDefVersionId = formDefinitionRS.getInt("form_definition_version_id");
                    log.info("moving data for formDefVersion "+formDefVersionId);
                    
                    // 2. determine the exported table name + binary column names
                    String xform = formDefinitionRS.getString("xform");
                    Pattern p = Pattern.compile("(instance id=\")([^\"]*)");
                    Matcher m = p.matcher(xform);
                    m.find();
                    String formBinding = m.group(2);
                    log.info("Found formBinding (exported table name): "+formBinding);
                    
                    List<String> columnBindings = new ArrayList<String>();
                    //<xf:bind id="coughsound" nodeset="/patientreg/coughsound" type="xsd:base64Binary" format="audio"/>
                    p = Pattern.compile("(bind id=\")([^\"]*)([^>]*)(type=\")([^\"]*)");
                    m = p.matcher(xform);
                    boolean more = true;
                    while (more) {
            	        more = m.find();
            	        if (more != false) {
            	        	String columnType = m.group(5); 
            	        	if (columnType.equals("xsd:base64Binary")) {
            	        		String columnBinding = m.group(2);
            	        		columnBindings.add(columnBinding);
        	                    log.info("Found base64Binary column: "+columnBinding);
            	        	}
            	        }
                    }
                    
                    // 3. create table update statements to change column type - if table doesn't exist, abort
                    for (String columnBinding : columnBindings) {
                    	String alterTableSql = String.format(alterExportedFormTable, formBinding, columnBinding);
                    	log.info("Running sql: "+alterTableSql);
                    	PreparedStatement alterTableStatement = c.prepareStatement(alterTableSql);
                    	try {
                    		alterTableStatement.execute();
                    	} catch (SQLException e) {
                    		log.warn("Could not alter table "+formBinding+". Aborting fix for this table (perhaps no exports have occurred).", e);
                    		break;
                    	} finally {
                    		alterTableStatement.close();
                    	}
                    }
                    
                    // 4. find all the form data inserted for these forms
                    formDataStatement.setInt(1, formDefVersionId);
                    ResultSet formDataRS = formDataStatement.executeQuery();
                    while (formDataRS.next()) {
                    	Integer formDataId = formDataRS.getInt(1);
                    	String data = formDataRS.getString(2);
                    	// 5. figure out which form_data contains binary data
                    	for (String columnBinding : columnBindings) {
                    		String regex = "(<"+columnBinding+">)(.*)(</"+columnBinding+">)";
                    		p = Pattern.compile(regex);
    	                    m = p.matcher(data);
    	                    more = m.find();
    	                    if (more != false) {
    	                    	String columnData = m.group(2);
    	                    	log.info("Found column data for column: "+ columnBinding +", formData: "+formDataId);
    	                    	if (columnData != null) {
	                    			// 6. update data in exported table for each affected form data
	                    			byte decoded[] = Base64.decodeBase64(columnData.getBytes());
									String alterDataSql = String.format(alterExportedBinaryData, formBinding, columnBinding);
									PreparedStatement alterDataStatement = c.prepareStatement(alterDataSql);
									alterDataStatement.setBytes(1, decoded);
									alterDataStatement.setInt(2, formDataId);
									try {
										alterDataStatement.execute();
			                    	} catch (SQLException e) {
			                    		log.warn("Could not alter binary data "+columnBinding+" in table "+formBinding, e);
			                    		break;
			                    	} finally {
			                    		alterDataStatement.close();
			                    	}
    	                    	}
    	                    }
                    	}
                    }
                }
            } finally {
            	formDataStatement.close();
            	formDefinitionVersionStatement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return new SqlStatement[] { };
    }

    @Override
	public String getConfirmationMessage() {
        return "Binary form data has been exported successfully";
    }

    @Override
	public void setFileOpener(FileOpener arg0) {
        // do nothing
    }

    @Override
	public void setUp() throws SetupException {
        // do nothing
    }

    @Override
	public void validate(Database arg0) throws InvalidChangeDefinitionException {
        // do nothing
    }

}
