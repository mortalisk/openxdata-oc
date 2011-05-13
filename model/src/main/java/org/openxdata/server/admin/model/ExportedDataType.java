package org.openxdata.server.admin.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Defines the type of the exported data field. Do not create an instance
 * of ExportedDataType - use the method getDataType(Serializable object) to
 * retrieve the correct instance of the inner classes that extend ExportedDataType.
 * 
 * All attributes of this class and its descendants MUST implement
 * {@link Serializable}
 */
public class ExportedDataType implements Serializable {

    private static final long serialVersionUID = 2169537650915359821L;
	
    /**
     * Translates the Serializable object into the appropriate ExportedDataType subclass
     * @param object Serializable object to convert
     * @return ExportedDataType matching the Serializable type, or null if no match found
     */
    public static ExportedDataType getDataType(Serializable object) {
        if (object instanceof Boolean)
            return new DataTypeBoolean((Boolean) object);
        if (object instanceof Integer)
            return new DataTypeInteger((Integer) object);
        if (object instanceof String)
            return new DataTypeString((String) object);
        if (object instanceof Date)
            return new DataTypeDate((Date) object);
        if (object instanceof Double)
            return new DataTypeDouble((Double) object);
        // note: does not support BigDecimal or BigInteger due to constraints in GWT
        return null;
    }

	public Object getValue() {
	    // do nothing (this class should be abstract, but GWT doesn't allow that)
		return null;
	}

	public void setValue(Object value) { 
	    // do nothing
	}

	public static class DataTypeBoolean extends ExportedDataType {

		private static final long serialVersionUID = 7550242288947318493L;

		Boolean value;
		
		public DataTypeBoolean() {}

		public DataTypeBoolean(Boolean value) {
			this.value = value;
		}

		@Override
		public Object getValue() {
			return value;
		}

		@Override
		public void setValue(Object value) {
			this.value = (Boolean) value;
		}

	}

	public static class DataTypeInteger extends ExportedDataType {

		private static final long serialVersionUID = -91987751784726386L;

		Integer value;
		
        public DataTypeInteger() {}		

		public DataTypeInteger(Integer value) {
			this.value = value;
		}

		@Override
		public Object getValue() {
			return value;
		}

		@Override
		public void setValue(Object value) {
			this.value = (Integer) value;
		}
	}

	public static class DataTypeString extends ExportedDataType {

		private static final long serialVersionUID = 3727540185611034640L;

		String value;

		public DataTypeString() {}

		public DataTypeString(String value) {
			this.value = value;
		}

		@Override
		public Object getValue() {
			return value;
		}

		@Override
		public void setValue(Object value) {
			this.value = (String) value;
		}
	}

	public static class DataTypeDate extends ExportedDataType {

		private static final long serialVersionUID = 1816753120632929210L;

		Date value;
		
        public DataTypeDate() {}

		public DataTypeDate(Date value) {
			this.value = value;
		}

		@Override
		public Object getValue() {
			return value;
		}

		@Override
		public void setValue(Object value) {
			this.value = (Date) value;
		}
	}

	public static class DataTypeDouble extends ExportedDataType {

		private static final long serialVersionUID = -623606810559173654L;

		Double value;

        public DataTypeDouble() {}
        
		public DataTypeDouble(Double value) {
			this.value = value;
		}

		@Override
		public Object getValue() {
			return value;
		}

		@Override
		public void setValue(Object value) {
			this.value = (Double) value;
		}
	}
}
