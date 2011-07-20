package org.openxdata.server.database.liquibase.customchanges;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class ReExportAllFormData implements CustomSqlChange {
    
    private Logger log = LoggerFactory.getLogger(ReExportAllFormData.class);
    Map<Integer, String> formBindingCache = new HashMap<Integer, String>();

    @Override
	public SqlStatement[] generateStatements(Database db) throws UnsupportedChangeException, CustomChangeException {
        DatabaseConnection c = db.getConnection();
        
        try {
            // finds all the old form_data archives
            PreparedStatement formDataArchiveStatement = c.prepareStatement(
                    "select fda.form_definition_version_id, fda.description, fda.data, " +
                    "fda.creator, fda.date_created, fda.changed_by, fda.date_changed " +
                    "from form_data_archive fda");
            
            // inserts data into form_data table (with status - no export)
            PreparedStatement insertFormDataStatement = c.prepareStatement(
                    "insert into form_data (form_definition_version_id, description, data, " +
                        "creator, date_created, changed_by, date_changed, exported) " +
                    "values (?, ?, ?, ?, ?, ?, ?, 0)");
            
            // gets the formDefVersion (so we can determine the form binding for the exported table name)
            PreparedStatement getFormDefVersionStatement = c.prepareStatement(
                    "select fdv.xform from form_definition_version fdv where fdv.form_definition_version_id = ?");
            
            // drops export table (so they can be created again with copied form_data)
            PreparedStatement dropExportTableStatement = null; 
            
            try {
                ResultSet formDataArchiveRS = formDataArchiveStatement.executeQuery();
                while (formDataArchiveRS.next()) {
                    Integer formDefVersionId = formDataArchiveRS.getInt("form_definition_version_id");
                    log.info("moving data for formDefVersion "+formDefVersionId);
                    
                    // finds out the form binding for this form def version
                    String formBinding = formBindingCache.get(formDefVersionId);
                    if (formBinding == null) {
                        getFormDefVersionStatement.setInt(1, formDefVersionId);
                        ResultSet formDefVersionRS = getFormDefVersionStatement.executeQuery();
                        if (formDefVersionRS.next()) { // should only be one result
                            String xform = formDefVersionRS.getString(1); // <xf:instance id="newform">
                            Pattern p = Pattern.compile("(instance id=\")([^\"]*)");
                            Matcher m = p.matcher(xform);
                            m.find();
                            formBinding = m.group(2);
                            log.info("Found formBinding (for exported table name) :"+formBinding);
                            formBindingCache.put(formDefVersionId, formBinding);
                        }
                    }
                    
                    // deletes the exported tables for a fresh start
                    try {
                        // drops export table (so they can be created again with copied form_data)
                        dropExportTableStatement = c.prepareStatement("drop table if exists "+formBinding);
                        dropExportTableStatement.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    
                    // moves form archives back to form data table
                    insertFormDataStatement.setInt(1, formDefVersionId);
                    insertFormDataStatement.setString(2, formDataArchiveRS.getString("description"));
                    insertFormDataStatement.setString(3, formDataArchiveRS.getString("data"));
                    insertFormDataStatement.setInt(4, formDataArchiveRS.getInt("creator"));
                    insertFormDataStatement.setDate(5, formDataArchiveRS.getDate("date_created"));
                    Integer changedBy = formDataArchiveRS.getInt("changed_by");
                    if (changedBy == 0) {
                        insertFormDataStatement.setNull(6, Types.INTEGER);
                    } else {
                        insertFormDataStatement.setInt(6, changedBy);
                    }
                    insertFormDataStatement.setDate(7, formDataArchiveRS.getDate("date_changed"));
                    try {
                        insertFormDataStatement.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            } finally {
                formDataArchiveStatement.close();
                insertFormDataStatement.close();
                getFormDefVersionStatement.close();
                if (dropExportTableStatement != null) {
                    dropExportTableStatement.close();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return new SqlStatement[] { };
    }

    @Override
	public String getConfirmationMessage() {
        return "Archived form data has been exported successfully";
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
