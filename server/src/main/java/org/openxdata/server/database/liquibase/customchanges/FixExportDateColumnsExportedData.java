package org.openxdata.server.database.liquibase.customchanges;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.openxdata.server.admin.model.exception.UnexpectedException;
import org.openxdata.server.export.rdbms.engine.Functions;
import org.openxdata.server.util.XmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import liquibase.FileOpener;
import liquibase.change.custom.CustomSqlChange;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.sql.SqlStatement;
import liquibase.exception.CustomChangeException;
import liquibase.exception.InvalidChangeDefinitionException;
import liquibase.exception.SetupException;
import liquibase.exception.UnsupportedChangeException;

public class FixExportDateColumnsExportedData implements CustomSqlChange {

	private Logger log = LoggerFactory.getLogger(FixExportDateColumnsExportedData.class);
	private static final String ADD_EXPORT_DATE_COLUMN_SQL = "ALTER table %s ADD COLUMN openxdata_form_data_date_created DATETIME;";
	private static final String GET_FORM_DATA_SQL = "SELECT fd.form_data_id, fd.date_created FROM form_data fd where fd.form_definition_version_id=? and fd.exported&1=1";
	private static final String GET_EXPORTED_FORM_DATA_TABLE_SQL = "SELECT * FROM %s where openxdata_form_data_id=?";       
	private static final String UPDATE_EXPORT_DATE_COLUMN_SQL = "UPDATE %s SET openxdata_form_data_date_created=? WHERE openxdata_form_data_id=?";     
	private static final String GET_ALL_FORM_DEFNS_SQL = "SELECT fdv.form_definition_version_id, fdv.xform FROM form_definition_version fdv";   
	private DatabaseConnection dbConnection;

	@Override
	public SqlStatement[] generateStatements(Database db) throws UnsupportedChangeException, CustomChangeException {
		initialiseDbConnection(db);
		try {
			processAllFormDefinitions();
		}catch (SQLException e) {
			log.warn("Could not apply fix to add Date_Created to Exported Form_Data", e);
		}
		return new SqlStatement[] { };
	}

	private void initialiseDbConnection(Database db) {
		if (dbConnection == null) {
			dbConnection = db.getConnection();
		}
	}

	private void processAllFormDefinitions() throws SQLException {
		PreparedStatement formDefinitionVersionStatement = dbConnection.prepareStatement(GET_ALL_FORM_DEFNS_SQL);
		
		try {
			ResultSet formDefinitionRS = formDefinitionVersionStatement.executeQuery();
	
			// Step 1 - Loop through all the forms. 
			while (formDefinitionRS.next()) {
				Integer formDefVersionId = formDefinitionRS.getInt("form_definition_version_id");
				String xform = formDefinitionRS.getString("xform");
				if (xform != null && !xform.trim().equals("")) {
					List<String> tableNames = getExportedTableNames(XmlUtil.fromString2Doc(xform));
					processFormData(tableNames, formDefVersionId);
				}
			}
		} finally {
			formDefinitionVersionStatement.close();
		}
	}

	private void processFormData(List<String> tableNames, Integer formDefVersionId) throws SQLException {
		// Step 2 - Loop through all form data that has been exported. 
		boolean addedColumn = false;
		PreparedStatement exportedTableStatement = dbConnection.prepareStatement(GET_FORM_DATA_SQL);
		try {
			exportedTableStatement.setInt(1, formDefVersionId);
			ResultSet formDataRS = exportedTableStatement.executeQuery();
			while (formDataRS.next()) {
				Integer formDataId = formDataRS.getInt(1);
				if (!addedColumn) {
					addExportDateColumn(tableNames, formDataId);
					addedColumn = true;
				}
				Timestamp dateCreated = formDataRS.getTimestamp(2);
				updateExportDateColumn(tableNames, formDataId, dateCreated);
			}
		} finally {
			exportedTableStatement.close();
		}
	}
	
	private void addExportDateColumn(List<String> tableNames, Integer formDataId) throws SQLException {
		// Step 3 - Add the date created column.
		for (String tableName : tableNames) {
			String selectAllColumnsSql = String.format(GET_EXPORTED_FORM_DATA_TABLE_SQL, tableName);
			PreparedStatement exportedColumnDataStatement = dbConnection.prepareStatement(selectAllColumnsSql);
			try {
				exportedColumnDataStatement.setString(1, formDataId.toString());
				ResultSet exportDataRS = exportedColumnDataStatement.executeQuery();
				if (!columnExists(exportDataRS.getMetaData(), "openxdata_form_data_date_created")) {
					addExportDateColumn(tableName);
				} else {
					log.warn("Table "+tableName+" already has the new column openxdata_form_data_date_created");
				}
			} catch (SQLException e) {
				// move on to the next exported table since this table has failed.
				log.warn("Could not determine columns in the table "+tableName, e);
			} finally {
				exportedColumnDataStatement.close();
			}
		}
	}

	private void updateExportDateColumn(List<String> tableNames, Integer formDataId, Timestamp dateCreated) throws SQLException  {
		// Step 4 - Update the date created column for the form data
		for (String tableName : tableNames) {
			String updateFormDataDateCreated = String.format(UPDATE_EXPORT_DATE_COLUMN_SQL, tableName);
			PreparedStatement updateExportDateStatement = dbConnection.prepareStatement(updateFormDataDateCreated);
			try {
				updateExportDateStatement.setTimestamp(1, dateCreated);
				updateExportDateStatement.setString(2, formDataId.toString());
				updateExportDateStatement.execute();
			} catch (SQLException e) {
				// move on to the next exported table since this table has failed.
				log.warn("Could not update data for the column openxdata_form_data_date_created in table "+tableName, e);
			} finally {
				updateExportDateStatement.close();
			}
		}
	}

	public void addExportDateColumn(String tableName) throws SQLException {
		String createColumnSql = String.format(ADD_EXPORT_DATE_COLUMN_SQL, tableName);
		Statement createColumnStatement = dbConnection.createStatement();
		try {
			createColumnStatement.executeUpdate(createColumnSql);  
		} catch (SQLException e) {
			log.warn("Could create column openxdata_form_data_date_created in table "+tableName, e);
		} finally {
			createColumnStatement.close();
		}
	}

	@Override
	public String getConfirmationMessage() {
		return "Export date column has been added successfully";
	}

	@Override
	public void setFileOpener(FileOpener arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setUp() throws SetupException {
		// TODO Auto-generated method stub
	}

	@Override
	public void validate(Database arg0) throws InvalidChangeDefinitionException {
		// TODO Auto-generated method stub
	}

	public boolean columnExists(ResultSetMetaData meta, String columnName) {
		boolean columnFound = false;
		try {
			int numCol = meta.getColumnCount();
			for (int i = 1; i < numCol+1; i++) {
				if(meta.getColumnName(i).equals(columnName)) {
					columnFound = true;
					break;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return columnFound;
	}

	@SuppressWarnings("unchecked")
	public List<String> getExportedTableNames(Document schemaDocument) {
		List<String> tables = new ArrayList<String>();
		if (schemaDocument == null) {
			throw new UnexpectedException("schemaDocument cannot be null");
		}
		NodeList instanceNodes = schemaDocument.getElementsByTagNameNS("*", "instance");

		if (instanceNodes.getLength() == 0) {
			return Collections.EMPTY_LIST;
		}
		Node instanceNode = instanceNodes.item(0);
		Functions.cleanNode(instanceNode);
		NodeList tableElements = instanceNode.getChildNodes();
		for (int i = 0; i < tableElements.getLength(); i++) {
			Node tableElement = tableElements.item(i);
			if (Functions.isValidNode(tableElement)) {
				determineIfElementIsTableName(schemaDocument, tableElement, tables);
				break;
			}
		}
		return tables;
	}

	private void determineIfElementIsTableName(Document schemaDocument, Node tableElement, List<String> tables) {
		if (!Functions.hasValidChildNodes(tableElement)) {
			return;
		}

		Functions.cleanNode(tableElement);
		String tableName = tableElement.getNodeName();
		if (StringUtils.isBlank(tableName)) {
			return;
		}

		NodeList columnElements = tableElement.getChildNodes();
		for (int index = 0; index < columnElements.getLength(); index++) {
			Node columnElement = columnElements.item(index);
			if (Functions.isValidNode(columnElement)) {
				Functions.cleanNode(columnElement);

				if (Functions.hasValidChildNodes(columnElement)) {
					if (Functions.isRepeat(schemaDocument, columnElement)) {
						determineIfElementIsTableName(schemaDocument, columnElement, tables);
						continue;
					} else {
						NodeList children = columnElement.getChildNodes();
						for (int child = 0; child < children.getLength(); child++) {
							Node childElement = children.item(child);
							if (Functions.isRepeat(schemaDocument, childElement)) {
								determineIfElementIsTableName(schemaDocument, childElement, tables);
								continue;
							}
						}
						continue;
					}
				}
			}
		}
		tables.add(tableName);
	}
}
