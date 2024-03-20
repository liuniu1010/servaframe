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

    private void insertEmployee(VersionEntity employee) {
        // perpare the task
        InsertEmployeeTask insertEmployeeTask = new InsertEmployeeTask(employee);

        // get db service from factory
        DBServiceIFC dbService = ServiceFactory.getDBService();
        // submit the add addemployee action
        dbService.executeSaveTask(insertEmployeeTask);
    } 

    private void insertEmployeeWithException(VersionEntity employee) {
        // perpare the task
        InsertEmployeeWithExceptionTask insertEmployeeWithExceptionTask = new InsertEmployeeWithExceptionTask(employee);

        // get db service from factory
        DBServiceIFC dbService = ServiceFactory.getDBService();
        // submit the add addemployee action
        dbService.executeSaveTask(insertEmployeeWithExceptionTask);
    }
 
    private void updateEmployee(VersionEntity employee) {
        // perpare the task
        UpdateEmployeeTask updateEmployeeTask = new UpdateEmployeeTask(employee);

        // get db service from factory
        DBServiceIFC dbService = ServiceFactory.getDBService();
        // submit the add addemployee action
        dbService.executeSaveTask(updateEmployeeTask);
    }
 
    private void deleteEmployee(VersionEntity employee) {
        // perpare the task
        DeleteEmployeeTask deleteEmployeeTask = new DeleteEmployeeTask(employee);

        // get db service from factory
        DBServiceIFC dbService = ServiceFactory.getDBService();
        // submit the add addemployee action
        dbService.executeSaveTask(deleteEmployeeTask);
    }

    private int getNumberOfEmployee() {
        DBServiceIFC dbService = ServiceFactory.getDBService();
        Object result = dbService.executeQueryTask(new CheckNumberOfEmployeeTask());
        return new Integer(result.toString()).intValue();
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

    public void testTransaction() {
        VersionEntity employee = generateEmployee();
        int numberBefore = getNumberOfEmployee();
        try {
            insertEmployeeWithException(employee);
        }
        catch(Exception ex) {
        } 
        int numberAfter = getNumberOfEmployee();
        assertEquals(numberBefore, 0);
        assertEquals(numberAfter, 0);
        System.out.println("testTransaction passed");
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

    private String getEmployeeName(long employeeId) {
        DBServiceIFC dbService = ServiceFactory.getDBService();
        return (String)dbService.executeQueryTask(new CheckNameTask(employeeId));
    }

    private List<Map<String, Object>> queryBySQL() {
        DBServiceIFC dbService = ServiceFactory.getDBService();
        return (List<Map<String, Object>>)dbService.executeQueryTask(new QueryBySQLTask());
    }

    private VersionEntity loadById(long employeeId) {
        DBServiceIFC dbService = ServiceFactory.getDBService();
        return (VersionEntity)dbService.executeQueryTask(new LoadByIdTask(employeeId));
    }

    public void testQueryScalar() {
        VersionEntity employee = generateEmployee();
        insertEmployee(employee);
        String name = getEmployeeName(employee.getId());
        assertEquals(name, "Tom");
        System.out.println("testQueryScalar passed");
    }

    public void testQuery() {
        VersionEntity employee = generateEmployee();
        insertEmployee(employee);
        List<Map<String, Object>> result = queryBySQL();
        assertTrue(result.size() > 0);
        System.out.println("testQuery passed");
    }

    public void testLoadById() {
        VersionEntity employee = generateEmployee();
        insertEmployee(employee);
        VersionEntity versionEntity = loadById(employee.getId());
        assertEquals(versionEntity.getAttribute("name"), "Tom");
        System.out.println("testLoadById passed");
    }
}

class InsertEmployeeTask implements DBSaveTaskIFC {
    private VersionEntity employee;
    public InsertEmployeeTask(VersionEntity inputEmployee) {
        employee = inputEmployee;
    }

    @Override
    public Object save(DBConnectionIFC dbConnection) {
        try {
            dbConnection.insert(employee);
            return null;
        }
        catch(SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}

class InsertEmployeeWithExceptionTask implements DBSaveTaskIFC {
    private VersionEntity employee;
    public InsertEmployeeWithExceptionTask(VersionEntity inputEmployee) {
        employee = inputEmployee;
    }

    @Override
    public Object save(DBConnectionIFC dbConnection) {
        try {
            dbConnection.insert(employee);
            if(true) {
                throw new RuntimeException("test roll back");
            }
            return null;
        }
        catch(SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}

class UpdateEmployeeTask implements DBSaveTaskIFC {
    private VersionEntity employee;
    public UpdateEmployeeTask(VersionEntity inputEmployee) {
        employee = inputEmployee;
    }

    @Override
    public Object save(DBConnectionIFC dbConnection) {
        try {
            dbConnection.update(employee);
            return null;
        }
        catch(SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}

class DeleteEmployeeTask implements DBSaveTaskIFC {
    private VersionEntity employee;
    public DeleteEmployeeTask(VersionEntity inputEmployee) {
        employee = inputEmployee;
    }

    @Override
    public Object save(DBConnectionIFC dbConnection) {
        try {
            dbConnection.delete(employee);
            return null;
        }
        catch(SQLException ex) {
            throw new RuntimeException(ex);
        }
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

class CheckNumberOfEmployeeTask implements DBQueryTaskIFC {
    @Override
    public Object query(DBConnectionIFC dbConnection) {
        try {
            String sql = "select count(*) as number from employee";
            return dbConnection.queryScalar(sql);
        }
        catch(SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}

class CheckNameTask implements DBQueryTaskIFC {
    private long employeeId;
    public CheckNameTask(long inputEmployeeId) {
        employeeId = inputEmployeeId;
    }

    @Override
    public Object query(DBConnectionIFC dbConnection) {
        try {
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
}

class QueryBySQLTask implements DBQueryTaskIFC {
    @Override
    public Object query(DBConnectionIFC dbConnection) {
        try {
            String sql = "select * from employee";
            return dbConnection.query(sql);
        }
        catch(SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}

class LoadByIdTask implements DBQueryTaskIFC {
    private long employeeId;
    public LoadByIdTask(long inputEmployeeId) {
        employeeId = inputEmployeeId;
    }

    @Override
    public Object query(DBConnectionIFC dbConnection) {
        try {
            return dbConnection.loadVersionEntityById("employee", employeeId);
        }
        catch(SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
