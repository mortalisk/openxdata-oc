package org.openxdata.server.admin.model.paging;

import java.io.Serializable;
import java.util.Date;

import org.openxdata.server.admin.model.exception.UnexpectedException;


public class FilterConfig implements Serializable {
	
	private static final long serialVersionUID = -5500689821800568751L;

	FilterComparison comparison = FilterComparison.LIKE;
	String field;
	FilterType type;
	String stringValue;
	Boolean booleanValue;
	Number numberValue;
	Date dateValue;
	
	public FilterConfig() {
		// default constructor
	}
	
	public FilterConfig(String field, String value) {
		this.field = field;
		this.stringValue = value;
		this.type = FilterType.STRING;
	}
	
	public FilterConfig(String field, Number value) {
		this.field = field;
		this.numberValue = value;
		this.type = FilterType.NUMERIC;
	}
	
	public static FilterConfig createFilterConfigGreaterThan(String field, Number value) {
		FilterConfig config = new FilterConfig(field, value);
		config.setComparison(FilterComparison.GREATER_THAN);
		return config;
	}
	
	public static FilterConfig createFilterConfigLessThan(String field, Number value) {
		FilterConfig config = new FilterConfig(field, value);
		config.setComparison(FilterComparison.LESS_THAN);
		return config;
	}
	
	public static FilterConfig createFilterConfigEqualTo(String field, Number value) {
		FilterConfig config = new FilterConfig(field, value);
		config.setComparison(FilterComparison.EQUAL_TO);
		return config;
	}
	
	public FilterConfig(String field, Date value) {
		this.field = field;
		this.dateValue = value;
		this.type = FilterType.DATE;
	}
	
	public static FilterConfig createFilterConfigBeforeDate(String field, Date value) {
		FilterConfig config = new FilterConfig(field, value);
		config.setComparison(FilterComparison.LESS_THAN);
		return config;
	}
	
	public static FilterConfig createFilterConfigAfterDate(String field, Date value) {
		FilterConfig config = new FilterConfig(field, value);
		config.setComparison(FilterComparison.GREATER_THAN);
		return config;
	}
	
	public static FilterConfig createFilterConfigOnDate(String field, Date value) {
		FilterConfig config = new FilterConfig(field, value);
		config.setComparison(FilterComparison.EQUAL_TO);
		return config;
	}
	
	public FilterConfig(String field, Boolean value) {
		this.field = field;
		this.booleanValue = value;
		this.type = FilterType.BOOLEAN;
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
