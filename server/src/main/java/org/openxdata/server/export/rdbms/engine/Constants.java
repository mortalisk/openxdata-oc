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

/**
 * Class that contains a list of Constants
 * @author Tumwebaze Charles
 *
 */
public class Constants {
	public static final String SQL_CREATE_TABLE="CREATE TABLE %s";
	public static final String OPENING_BRACE="(";
	public static final String CLOSING_BRACE=")";
    public static final String SQL_UPDATE="UPDATE %s SET %s WHERE %s;";
	
	public static final String TYPE_INTEGER="INTEGER";
	public static final String TYPE_VARCHAR="VARCHAR";
	public static final String TYPE_CHAR="CHAR";
	public static final String TYPE_DECIMAL="DECIMAL";
	public static final String TYPE_DATE="DATE";
	public static final String TYPE_TIME="TIME";
	public static final String TYPE_DATETIME="TIMESTAMP";
	public static final String TYPE_BOOLEAN="BOOLEAN";
	public static final String TYPE_BINARY="LONGBLOB";
	
	public static final String SIZE_OF_TYPE="(%d)";
	public static final String SIZE_OF_DECIMAL_TYPE="(%d,%d)";

    public static String ESCAPE_CHAR = "`";
}
