package org.openxdata.server.admin.model.paging;

import java.io.Serializable;
import java.util.Date;

import org.openxdata.server.admin.model.exception.UnexpectedException;


public class FilterConfig implements Serializable {
	
	private static final long serialVersionUID = -5500689821800568751L;

	FilterComparison comparison;
	String field;
	FilterType type;
	String stringValue;
	Boolean booleanValue;
	Number numberValue;
	Date dateValue;
	
	public FilterConfig() {
		// default constructor
	}
	
	public FilterConfig(String field, Object value, FilterComparison comparison) {
		this.field = field;
		this.comparison = comparison;
		setValue(value);
	}
	
	public FilterComparison getComparison() {
		return comparison;
	}

	public void setComparison(FilterComparison comparison) {
		this.comparison = comparison;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public FilterType getType() {
		return type;
	}

	public void setType(FilterType type) {
		this.type = type;
	}
	
	public Object getValue() {
		switch (type) {
			case BOOLEAN : return booleanValue;
			case STRING : return stringValue;
			case DATE : return dateValue;
			case NUMERIC : return numberValue;
		}
    	return null;
    }
	
	public void setValue(Object value) {
		if (value instanceof Date) {
			this.dateValue = (Date)value;
			this.type = FilterType.DATE;
		} else if (value instanceof String) {
			this.stringValue = (String)value;
			this.type = FilterType.STRING;
		}  else if (value instanceof Boolean) {
			this.booleanValue = (Boolean)value;
			this.type = FilterType.BOOLEAN;
		}  else if (value instanceof Number) {
			this.numberValue = (Number)value;
			this.type = FilterType.NUMERIC;
		} else {
			throw new UnexpectedException("Value of type "+value.getClass()+" is not supported in Filter Config");
		}
    }
}
