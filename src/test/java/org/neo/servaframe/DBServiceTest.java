package org.neo.servaframe;

import java.util.*;
import java.sql.SQLException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.neo.servaframe.util.ConfigUtil;
import org.neo.servaframe.interfaces.*;
import org.neo.servaframe.model.*;

/**
 * Unit test for DBService
 */
public class DBServiceTest 
    extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public DBServiceTest( String testName ) {
        super( testName );
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // Code to set up resources or initialize variables before each test method
        cleanDatabase();
    }

    @Override
    protected void tearDown() throws Exception {
        // Code to clean up resources after each test method
        super.tearDown();
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite( DBServiceTest.class );
    }

    private void cleanDatabase() {
        CleanDatabaseTask cleanTask = new CleanDatabaseTask();
        DBServiceIFC dbService = ServiceFactory.getDBService();
        dbService.executeSaveTask(cleanTask);
    }

    private VersionEntity generateEmployee() {
        // create java entity of employee
        // set all necessary attribute
        VersionEntity employee = new VersionEntity("employee");
        employee.setAttribute("no", "1");
        employee.setAttribute("name", "Tom");
        employee.setAttribute("age", 23);
        employee.setAttribute("address", "auckland");
        employee.setAttribute("createDate", new Date());

        return employee;
    }

    public void testInsert() {
        VersionEntity employee = generateEmployee();
        int numberBefore = getNumberOfEmployee();
        insertEmployee(employee);
        int numberAfter = getNumberOfEmployee();
        assertEquals(numberBefore, 0);
        assertEquals(numberAfter, 1);
        System.out.println("testInsert passed");
    }

    private void insertEmployee(VersionEntity employee) {
        // instantiate DBService
        DBServiceIFC dbService = ServiceFactory.getDBService();

        // start to execute Task
        dbService.executeSaveTask(new DBSaveTaskIFC() {
            @Override
            public Object save(DBConnectionIFC dbConnection) {
                try {
                    // do business logic
                    dbConnection.insert(employee);
                    return null;
                }
                catch(SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    } 

    public void testTransaction() {
        VersionEntity employee = generateEmployee();
        int numberBefore = getNumberOfEmployee();
        try {
            insertEmployeeWithExceptionInTransaction(employee);
        }
        catch(Exception ex) {
        } 
        int numberAfter = getNumberOfEmployee();
        assertEquals(numberBefore, 0);
        assertEquals(numberAfter, 0);
        System.out.println("testTransaction passed");
    }

    public void testAutoCommit() {
        VersionEntity employee = generateEmployee();
        int numberBefore = getNumberOfEmployee();
        try {
            insertEmployeeWithExceptionInAutoCommit(employee);
        }
        catch(Exception ex) {
        } 
        int numberAfter = getNumberOfEmployee();
        assertEquals(numberBefore, 0);
        assertEquals(numberAfter, 1);
        System.out.println("testAutoCommit passed");
    }

    private void insertEmployeeWithExceptionInTransaction(VersionEntity employee) {
        // instantiate DBService
        DBServiceIFC dbService = ServiceFactory.getDBService();

        // start to execute Task
        dbService.executeSaveTask(new DBSaveTaskIFC() {
            @Override
            public Object save(DBConnectionIFC dbConnection) {
                try {
                    // do business logic
                    dbConnection.insert(employee);
                    if(true) {
                        // meet exception
                        throw new RuntimeException("test roll back");
                    }
                    return null;
                }
                catch(SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    private void insertEmployeeWithExceptionInAutoCommit(VersionEntity employee) {
        // instantiate DBService
        DBServiceIFC dbService = ServiceFactory.getDBService();

        // start to execute Task
        dbService.executeAutoCommitSaveTask(new DBAutoCommitSaveTaskIFC() {
            @Override
            public Object autoCommitSave(DBConnectionIFC dbConnection) {
                try {
                    // do business logic
                    dbConnection.insert(employee);
                    if(true) {
                        // meet exception
                        throw new RuntimeException("test roll back");
                    }
                    return null;
                }
                catch(SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }
 
    public void testUpdate() {
        VersionEntity employee = generateEmployee();
        insertEmployee(employee);
        employee.setAttribute("name", "Tom2");
        updateEmployee(employee);
        VersionEntity employee2 = loadById(employee.getId());
        assertEquals(employee2.getAttribute("name"), "Tom2");
        System.out.println("testUpdate passed");
    }

    private void updateEmployee(VersionEntity employee) {
        // instantiate DBService
        DBServiceIFC dbService = ServiceFactory.getDBService();

        // start to execute Task
        dbService.executeSaveTask(new DBSaveTaskIFC() {
            @Override
            public Object save(DBConnectionIFC dbConnection) {
                try {
                    // do business logic
                    dbConnection.update(employee);
                    return null;
                }
                catch(SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }
 
    public void testDelete() {
        VersionEntity employee = generateEmployee();
        insertEmployee(employee);
        int numberBefore = getNumberOfEmployee();
        deleteEmployee(employee);
        int numberAfter = getNumberOfEmployee();
        assertEquals(numberBefore, 1);
        assertEquals(numberAfter, 0);
        System.out.println("testDelete passed");
    }

    private void deleteEmployee(VersionEntity employee) {
        // instantiate DBService
        DBServiceIFC dbService = ServiceFactory.getDBService();

        // start to execute Task
        dbService.executeSaveTask(new DBSaveTaskIFC() {
            @Override
            public Object save(DBConnectionIFC dbConnection) {
                try {
                    // do business logic
                    dbConnection.delete(employee);
                    return null;
                }
                catch(SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    private int getNumberOfEmployee() {
        // instantiate DBService
        DBServiceIFC dbService = ServiceFactory.getDBService();

        // start to execute Task
        Object result = dbService.executeQueryTask(new DBQueryTaskIFC() {
            @Override
            public Object query(DBConnectionIFC dbConnection) {
                try {
                    // do business logic
                    String sql = "select count(*) as number from employee";
                    return dbConnection.queryScalar(sql);
                }
                catch(SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        return new Integer(result.toString()).intValue();
    }

    public void testQueryScalar() {
        VersionEntity employee = generateEmployee();
        insertEmployee(employee);
        String name = getEmployeeName(employee.getId());
        assertEquals(name, "Tom");
        System.out.println("testQueryScalar passed");
    }

    private String getEmployeeName(String employeeId) {
        // instantiate DBService
        DBServiceIFC dbService = ServiceFactory.getDBService();

        // start to execute Task
        return (String)dbService.executeQueryTask(new DBQueryTaskIFC() {
            @Override
            public Object query(DBConnectionIFC dbConnection) {
                try {
                    // do business logic
                    String sql = "select name from employee where id = ?";
                    List<Object> params = new ArrayList<Object>();
                    params.add(employeeId);
                    SQLStruct sqlStruct = new SQLStruct(sql, params);
                    return dbConnection.queryScalar(sqlStruct);
                }
                catch(SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    public void testQuery() {
        VersionEntity employee = generateEmployee();
        insertEmployee(employee);
        List<Map<String, Object>> result = queryBySQL();
        assertTrue(result.size() > 0);
        System.out.println("testQuery passed");
    }

    private List<Map<String, Object>> queryBySQL() {
        // instantiate DBService
        DBServiceIFC dbService = ServiceFactory.getDBService();

        // start to execute Task
        return (List<Map<String, Object>>)dbService.executeQueryTask(new DBQueryTaskIFC() {
            @Override
            public Object query(DBConnectionIFC dbConnection) {
                try {
                    // do business logic
                    String sql = "select * from employee";
                    return dbConnection.query(sql);
                }
                catch(SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    public void testLoadById() {
        VersionEntity employee = generateEmployee();
        insertEmployee(employee);
        VersionEntity versionEntity = loadById(employee.getId());
        assertEquals(versionEntity.getAttribute("name"), "Tom");
        System.out.println("testLoadById passed");
    }

    private VersionEntity loadById(String employeeId) {
        // instantiate DBService
        DBServiceIFC dbService = ServiceFactory.getDBService();

        // start to execute Task
        return (VersionEntity)dbService.executeQueryTask(new DBQueryTaskIFC() {
            @Override
            public Object query(DBConnectionIFC dbConnection) {
                try {
                    // do business logic
                    return dbConnection.loadVersionEntityById("employee", employeeId);
                }
                catch(SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }
}

class CleanDatabaseTask implements DBSaveTaskIFC {
    @Override
    public Object save(DBConnectionIFC dbConnection) {
        try {
            String sql = "delete from employee";
            dbConnection.execute(sql);
            return null;
        }
        catch(SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
