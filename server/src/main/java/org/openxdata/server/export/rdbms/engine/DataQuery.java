package org.openxdata.server.export.rdbms.engine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Defines an SQL query
 *
 * @author dagmar@cell-life.org.za
 */
public class DataQuery implements Serializable {

    private static final long serialVersionUID = 5211999194730024938L;

    private String sql;
    private String tableName;
    private String columnNames;
    private String values;
    private List<Object> parameters = new ArrayList<Object>();
    
    public DataQuery() {
        
    }
    
    /**
     * Creates a DataQuery
     * @param sqlStatement String Constants.SQL_INSERT_INTO or Constants.SQL_UPDATE
     * @param tableName String name of table in database
     * @param columnNames String comma separated list of column names
     * @param values String comma separated list of ? matching column names
     * @param parameters List of parameters containing the actual values
     */
    public DataQuery(String sqlStatement, String tableName, String columnNames, String values, List<Object> parameters, String escapeChar) {
    	this.tableName = tableName;
    	this.columnNames = columnNames;
    	this.values = values;
    	this.parameters = parameters;
    	this.sql = String.format(sqlStatement, escapeChar + tableName + escapeChar, columnNames.toString(), values.toString());
    }
    
    public DataQuery(String sql, List<Object> parameters) {
    	this.parameters = parameters;
    	this.sql = sql;
    }
    
    public DataQuery(String sql, Object[] parameters) {
        for (Object param : parameters) {
            this.parameters.add(param);
        }
        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
    
    public Object getParameter(String columnName) {
    	String[] columns = columnNames.split(",");
    	for (int i=0, j=columns.length; i<j; i++) {
    		String column = columns[i];
    		if (column.equalsIgnoreCase(columnName)) {
    			return parameters.get(i);
    		}
    	}
    	return  null;
    }

    public List<Object> getParameters() {
        return parameters;
    }

    public void setParameters(List<Object> parameters) {
        this.parameters = parameters;
    }

    public String getTableName() {
		return tableName;
	}

	public String getColumnNames() {
		return columnNames;
	}

	public String getValues() {
		return values;
	}

	@Override
	public boolean equals(Object obj) {
		List<String> excludeFields = new ArrayList<String>();
		excludeFields.add("values");
		excludeFields.add("columnNames");
		excludeFields.add("tableName");
        return EqualsBuilder.reflectionEquals(this, obj, excludeFields);
    }

    @Override
	public int hashCode() {
		List<String> excludeFields = new ArrayList<String>();
		excludeFields.add("values");
		excludeFields.add("columnNames");
		excludeFields.add("tableName");
        return HashCodeBuilder.reflectionHashCode(this, excludeFields);
    }

    @Override
	public String toString() {
    	return new ToStringBuilder(this).
        	append("sql", sql).
        	append("parameters", parameters).
        	toString();
    }
}
