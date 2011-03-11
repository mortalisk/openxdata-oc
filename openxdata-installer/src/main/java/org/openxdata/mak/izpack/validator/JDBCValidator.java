package org.openxdata.mak.izpack.validator;

import com.izforge.izpack.installer.AutomatedInstallData;
import com.izforge.izpack.installer.DataValidator;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Validates the JDBC Mysql Connection in the MySql.Panel
 * @author Ronald.K
 */
public class JDBCValidator implements DataValidator {

        private String errorId, userName, password,
                host, port, database;

        public Status validateData(AutomatedInstallData adata) {
                Status status = Status.ERROR;

                try {

                        userName = adata.getVariable("mysql.username");
                        password = adata.getVariable("mysql.password.user");
                        host = adata.getVariable("mysql.host");
                        port = adata.getVariable("mysql.port");
                        database = adata.getVariable("mysql.openxdata.dbname");
                        System.out.println("User=" + userName + " password=" + password + " host=" + host + " database=" + database);

                        loadDBDriver();

                        if (canConnect(host, port, userName, password)
                                && dBExists(host, port, userName, password, database)) {
                                System.out.println("SuccessFull Logged in ");
                                status = Status.OK;
                        }

                        if (status == Status.OK) {
                                if (!userNameExists(adata))
                                        status = Status.ERROR;
                        }

                } catch (Throwable ex) {
                        errorId = ex.getLocalizedMessage();
                }
                return status;
        }

        public String getErrorMessageId() {
                return errorId;
        }

        public String getWarningMessageId() {
                return errorId;
        }

        public boolean getDefaultAnswer() {
                return true;
        }

        /**
         * Checks if the super user can login
         * @param host
         * @param port
         * @param username
         * @param password
         * @return
         */
        private boolean canConnect(String host, String port, String username, String password) {
                boolean success = false;
                try {
                        String url = getUrl(host, port, username, password, null);
                        Connection conn = DriverManager.getConnection(url);
                        conn.close();
                        success = true;
                } catch (SQLException ex) {
                        errorId = ex.getLocalizedMessage();
                        if (errorId.contains("UnknownHostException"))
                                errorId = "Unknown Host Name!!";
                } catch (Exception ex) {
                        errorId = ex.getLocalizedMessage();
                }
                return success;
        }

        /**
         * Checks if a database exists.
         * @param host
         * @param port
         * @param username
         * @param password
         * @param database
         * @return
         */
        private boolean dBExists(String host, String port, String username, String password, String database) {
                boolean success = true;
                try {
                        String url = getUrl(host, port, username, password, database);
                        Connection conn = DriverManager.getConnection(url);
                        conn.close();
                        success = false;//login was successful to the database and dbname hence DBname exists.
                        //this is wrong.Db is not supposed to exist
                        errorId = "Data Base Already Exists";
                } catch (SQLException ex) {
                }
                return success;
        }

        /**
         * Loads the JDBC Driver
         * @throws Throwable
         */
        private void loadDBDriver() throws Throwable {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
        }

        /**
         * Generates the JDBC url
         * @param host
         * @param port
         * @param username
         * @param password
         * @param database
         * @return
         */
        private String getUrl(String host, String port, String username, String password, String database) {
                StringBuffer url = new StringBuffer();
                url.append("jdbc:mysql://");
                url.append(host).append(":").append(port).append("/");

                if (database != null)
                        url.append(database);

                url.append("?user=").append(username).append("&password=").append(password);
                System.out.println(url);
                return url.toString();
        }

        /**
         * Checks if user name exists in the DB by checking in mysql tables user the root
         * user. If user exists then try to login with supplied credentials for openxdata.
         * Return true if the oxd username supplied is able to login with the password suplied
         * @param adata
         * @return
         */
        private boolean userNameExists(AutomatedInstallData adata) {
                boolean success = false;
                try {
                        String oxdUserName = adata.getVariable("mysql.openxdata.username");
                        String oxdPassword = adata.getVariable("mysql.openxdata.password");

                        String url = getUrl(host, port, userName, password, null);
                        Connection conn = DriverManager.getConnection(url);

                        Statement statement = conn.createStatement();

                        //Check if user exists
                        ResultSet rs = statement.executeQuery(
                                "SELECT * FROM mysql.user WHERE user LIKE '" + oxdUserName + "'");
                        if (rs.first()) {
                                rs.close();
                                statement.close();
                                conn.close();
                                //At this level user exists...Try to login with the suppled oxd password and username
                                url = getUrl(host, port, oxdUserName, oxdPassword, null);

                                try {
                                        conn = DriverManager.getConnection(url);
                                        success = true;//login was successful set success to true
                                } catch (SQLException sQLException) {
                                        errorId = "OpenXData UserName Exists But Wrong Password Was Provided";
                                }
                                conn.close();
                        } else {//oxduser does not exist in db...hence it is fine to proceed
                                success = true;
                                rs.close();
                                statement.close();
                                conn.close();
                        }
                } catch (Exception ex) {
                        errorId = ex.getLocalizedMessage();
                }
                return success;
        }
}
