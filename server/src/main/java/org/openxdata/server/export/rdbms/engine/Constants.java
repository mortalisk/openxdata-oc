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
