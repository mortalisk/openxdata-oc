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
package org.openxdata.server.dao.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.apache.commons.lang.Validate;

import org.apache.log4j.Logger;
import org.openxdata.server.admin.model.exception.OpenXdataDataAccessException;
import org.openxdata.server.dao.RdmsExporterDAO;
import org.openxdata.server.export.rdbms.engine.DataQuery;

/**
 * Exporter providing all the functionality to the ServerTask responsible 
 * for exporting form data xml to relational database tables.
 * 
 * Note: Since the Exporter can export to a separate database (this is
 * configurable via the application), a direct connection is required (not
 * using the Spring data source). Also, since the tables are generated
 * from the xform, Hibernate cannot be used for data access - 
 * generating Hibernate mapping files at runtime is not recommended.
 * 
 * @author dagmar@cell-life.org.za
 */
public class JdbcRdmsExporterDAO implements RdmsExporterDAO {

    private Logger log = Logger.getLogger(this.getClass());
    private final String connectionUrl;
    
	/**
     * prepares for data export (sets the database and connection url)
     * @param database database for which to export
     * @param connectionUrl connection url for the database to export data to
     * @see {Constants}
     */
	public JdbcRdmsExporterDAO(String connectionUrl) {
        this.connectionUrl = connectionUrl;
        Validate.notEmpty(connectionUrl, "Connection url is not supplied");
	}

    @Override
	public boolean dataExists(Integer formDataId, String tableName) {
        String statement = "select count(*) from "+tableName+" where openxdata_form_data_id="+formDataId;
        Connection connection = getConnection();
		try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(statement);
            if (rs.next()) {
                Integer count = rs.getInt(1);
                if (count >= 1) {
                    return true;
                }
            }
		} catch (SQLException e) {
			throw new OpenXdataDataAccessException(e);
		}
		finally {
            closeConnection(connection);
        }
        return false;
    }

	@Override
	public boolean tableExists(String database, String tableName) {
    	String sql = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = ? AND table_name = ?;";
		Connection connection = getConnection();
		try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, database);
            statement.setString(2, tableName);
            
			return checkTableExistence(statement);
		} catch (SQLException e) {
			throw new OpenXdataDataAccessException(e);
		} finally {
			closeConnection(connection);
		}
	}
	
    @Override
	public void executeSql(String sql) {
        Connection connection = getConnection();
        try {
            if (sql.trim().length() > 0) {
                Statement st = connection.createStatement();
                log.debug("Executing SQL="+sql);
                st.execute(sql.trim());
            }
        } catch (SQLException e) {
        	throw new OpenXdataDataAccessException(e);
		} finally {
            closeConnection(connection);
        }
    }
    
    @Override
	public void executeSql(List<DataQuery> statements) {
        Connection con = getConnection();
        try {
            con.setAutoCommit(false);
            for (DataQuery query : statements) {
                PreparedStatement ps = con.prepareStatement(query.getSql());
                List<Object> parameters = query.getParameters();
                for (int i=0, j=parameters.size(); i<j; i++) {
                    Object obj = parameters.get(i);
                    ps.setObject(i+1, obj);
                }
                ps.execute();
            }
            con.commit();
        } catch (SQLException e) {
            try {
				con.rollback();
			} catch (SQLException ex) {
				// do nothing
			}
            throw new OpenXdataDataAccessException(e);
        } finally {
            closeConnection(con);
        }
    }

    private boolean checkTableExistence(PreparedStatement statement) throws SQLException {
        ResultSet set = statement.executeQuery();
        if (set.next()) {
            int value = set.getInt(1);
            return value == 1;
        }
        return false;
    }
    
    private Connection getConnection() throws OpenXdataDataAccessException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection(connectionUrl);
        } catch (ClassNotFoundException e) {
            throw new OpenXdataDataAccessException("Missing class/jar for the database connector specified.");
        } catch (SQLException e) {
        	throw new OpenXdataDataAccessException(e);
		}
    }

    /**
     * Closes the connection if it is open
     * @param connection
     */
    private void closeConnection(Connection connection) {
		try {
			if(connection != null)
				connection.close();
		} catch (SQLException e) {
			throw new OpenXdataDataAccessException(e);
		}
	}
}