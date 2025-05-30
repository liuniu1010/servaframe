package org.neo.servaframe.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;

import org.neo.servaframe.interfaces.DBServiceIFC;
import org.neo.servaframe.interfaces.DBSaveTaskIFC;
import org.neo.servaframe.interfaces.DBAutoCommitSaveTaskIFC;
import org.neo.servaframe.interfaces.DBQueryTaskIFC;
import org.neo.servaframe.interfaces.DBConnectionIFC;
import org.neo.servaframe.util.ConfigUtil;

/***
 * Service to visit DB which encapsulate the
 * actions of getting connection, begin transaction
 * commit transaction and release connection
 *
 */
public class DBService implements DBServiceIFC {
    public DBService() {
    }

    @Override
    public void init() {
        try{
            Class.forName(ConfigUtil.getJdbcDriver());
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object executeSaveTask(DBSaveTaskIFC saveTask) {
        DBConnectionIFC dbConnection = null;
        Connection conn = null;
        try{
            String dbUrl = ConfigUtil.getDbUrl();
            String dbUsername = ConfigUtil.getDbUsername();
            String dbPassword = ConfigUtil.getDbPassword();

            conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            initCharset(conn);
            conn.setAutoCommit(false);

            dbConnection = new DBConnection(conn);
            Object toReturn = saveTask.save(dbConnection);
            conn.commit();
            return toReturn;
        }
        catch(RuntimeException rex) {
            if(conn != null) {
                try {
                    conn.rollback();
                }
                catch(SQLException se2) {
                    throw new RuntimeException(se2);
                }
            }
            throw rex; 
        }
        catch(Exception se) {
            if(conn != null) {
                try {
                    conn.rollback();
                }
                catch(SQLException se2) {
                    throw new RuntimeException(se2);
                }
            }
            throw new RuntimeException(se.getMessage(), se);
        }
        finally {
            if(dbConnection != null) {
                dbConnection.close();
            }
        }
    }

    @Override
    public Object executeAutoCommitSaveTask(DBAutoCommitSaveTaskIFC saveTask) {
        DBConnectionIFC dbConnection = null;
        Connection conn = null;
        try{
            String dbUrl = ConfigUtil.getDbUrl();
            String dbUsername = ConfigUtil.getDbUsername();
            String dbPassword = ConfigUtil.getDbPassword();

            conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            initCharset(conn);
            conn.setAutoCommit(true);

            dbConnection = new DBConnection(conn);
            Object toReturn = saveTask.autoCommitSave(dbConnection);
            return toReturn;
        }
        catch(RuntimeException rex) {
            throw rex;
        }
        catch(Exception se) {
            throw new RuntimeException(se);
        }
        finally {
            if(dbConnection != null) {
                dbConnection.close();
            }
        }
    }

    @Override
    public Object executeQueryTask(DBQueryTaskIFC queryTask) {
        DBConnectionIFC dbConnection = null;
        try{
            String dbUrl = ConfigUtil.getDbUrl();
            String dbUsername = ConfigUtil.getDbUsername();
            String dbPassword = ConfigUtil.getDbPassword();

            Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            initCharset(conn);

            dbConnection = new DBConnection(conn);
            Object result = queryTask.query(dbConnection);

            return result;
        }
        catch(RuntimeException rex) {
            throw rex;
        }
        catch(Exception se) {
            throw new RuntimeException(se);
        }
        finally {
            if(dbConnection != null) {
                dbConnection.close();
            }
        }
    }

    private void initCharset(Connection conn) throws SQLException {
        String product = conn.getMetaData().getDatabaseProductName(); // "MySQL", "Oracle", "Microsoft SQL Server", …
        if (product != null && product.toLowerCase().contains("mysql")) {
            try (Statement st = conn.createStatement()) {
                st.execute("SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci");
            }
        }
    }

}
