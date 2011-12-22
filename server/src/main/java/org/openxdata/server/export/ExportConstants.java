package org.openxdata.server.export;

public final class ExportConstants {
	
	/*
	 * Export bits must be unique and of the form 2^n
	 */ 
	public static final Integer EXPORT_BIT_RDBMS = 1;
	public static final Integer EXPORT_BIT_HTTP_POST = 2;
	public static final Integer EXPORT_BIT_DHIS2 = 4;

	/** List of exporter values where the RDMS export bit is 0. (supports 5 exporters). See #560 */
	public static Integer[] RDMS_EXPORT_BIT_0 = { 0, 3, 5, 7, 9, 11, 13, 15, 17, 19, 21, 23, 25, 27, 29, 31 };
}

