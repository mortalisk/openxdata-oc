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

import java.util.List;
import org.openxdata.server.admin.model.FormData;
import org.openxdata.server.util.XmlUtil;
import org.w3c.dom.Document;

/**
 * Engine to group together all SQL building parts into a handy utility class that can be 
 * used by the RDMS data export server task.
 * 
 * @author dagmar@cell-life.org.za
 */
public class RdmsEngine {
    	
    /**
     * Generates sql which can be used to create the tables defined in the xform
     * @param xform String containing xform definition
     * @param dbms int indicating the type of database
     * @return List of TableQuery, will never be null
     */
    public static List<TableQuery> getStructureSql(String xform) {
        Document schemaDocument = XmlUtil.fromString2Doc(xform);
        StructureBuilder builder = new StructureBuilder();
        return builder.createStructure(schemaDocument);
    }

    /**
     * Generates sql which can be used to insert the form data into the generated tables
     * @param xform String containing xform definition
     * @param update boolean indicating if the formData is being updated or not
     * @param formData FormData containing the data to export
     * @param dbms int indicating the type of database
     * @return Map of Strings containing SQL which can be used to insert/update the form data, will never be null and a list of parameters for the query
     */
    public static List<DataQuery> getDataSql(String xform, FormData formData, boolean update) {
        Document schemaDocument = XmlUtil.fromString2Doc(xform);
        Document dataDocument = XmlUtil.fromString2Doc(formData.getData());
        
        DataBuilder dataBuilder = new DataBuilder(schemaDocument, dataDocument);
        return dataBuilder.buildData(formData, update);
    }
}