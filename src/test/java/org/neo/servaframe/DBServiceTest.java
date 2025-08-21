package org.neo.servaframe;

import java.util.*;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.neo.servaframe.interfaces.*;
import org.neo.servaframe.model.*;

/**
 * Unit test for DBService
 */
class DBServiceTest {

    @BeforeEach
    void setUp() throws Exception {
        cleanDatabase();
    }

    @AfterEach
    void tearDown() throws Exception {
        // no-op (kept to mirror original structure)
    }

    private void cleanDatabase() {
        CleanDatabaseTask cleanTask = new CleanDatabaseTask();
        DBServiceIFC dbService = ServiceFactory.getDBService();
        dbService.executeSaveTask(cleanTask);
    }

    private VersionEntity generateEmployee() {
        VersionEntity employee = new VersionEntity("employee");
        employee.setAttribute("no", "1");
        employee.setAttribute("name", "Tom");
        employee.setAttribute("age", 23);
        employee.setAttribute("address", "auckland at 📝 ");
        employee.setAttribute("createDate", new Date());
        return employee;
    }

    @Test
    void testInsert() {
        VersionEntity employee = generateEmployee();
        int numberBefore = getNumberOfEmployee();
        insertEmployee(employee);
        int numberAfter = getNumberOfEmployee();
        assertEquals(0, numberBefore);
        assertEquals(1, numberAfter);
        System.out.println("testInsert passed");
    }

    private void insertEmployee(VersionEntity employee) {
        DBServiceIFC dbService = ServiceFactory.getDBService();
        dbService.executeSaveTask(new DBSaveTaskIFC() {
            @Override
            public Object save(DBConnectionIFC dbConnection) {
                try {
                    dbConnection.insert(employee);
                    return null;
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    @Test
    void testTransaction() {
        VersionEntity employee = generateEmployee();
        int numberBefore = getNumberOfEmployee();
        try {
            insertEmployeeWithExceptionInTransaction(employee);
        } catch (Exception ex) {
            // swallow like original
        }
        int numberAfter = getNumberOfEmployee();
        assertEquals(0, numberBefore);
        assertEquals(0, numberAfter);
        System.out.println("testTransaction passed");
    }

    @Test
    void testAutoCommit() {
        VersionEntity employee = generateEmployee();
        int numberBefore = getNumberOfEmployee();
        try {
            insertEmployeeWithExceptionInAutoCommit(employee);
        } catch (Exception ex) {
            // swallow like original
        }
        int numberAfter = getNumberOfEmployee();
        assertEquals(0, numberBefore);
        assertEquals(1, numberAfter);
        System.out.println("testAutoCommit passed");
    }

    private void insertEmployeeWithExceptionInTransaction(VersionEntity employee) {
        DBServiceIFC dbService = ServiceFactory.getDBService();
        dbService.executeSaveTask(new DBSaveTaskIFC() {
            @Override
            public Object save(DBConnectionIFC dbConnection) {
                try {
                    dbConnection.insert(employee);
                    if (true) {
                        throw new RuntimeException("test roll back");
                    }
                    return null;
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    private void insertEmployeeWithExceptionInAutoCommit(VersionEntity employee) {
        DBServiceIFC dbService = ServiceFactory.getDBService();
        dbService.executeAutoCommitSaveTask(new DBAutoCommitSaveTaskIFC() {
            @Override
            public Object autoCommitSave(DBConnectionIFC dbConnection) {
                try {
                    dbConnection.insert(employee);
                    if (true) {
                        throw new RuntimeException("test roll back");
                    }
                    return null;
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    @Test
    void testUpdate() {
        VersionEntity employee = generateEmployee();
        insertEmployee(employee);
        employee.setAttribute("name", null);
        updateEmployee(employee);
        VersionEntity employee2 = loadById(employee.getId());
        assertNull(employee2.getAttribute("name"));
        System.out.println("testUpdate passed");
    }

    private void updateEmployee(VersionEntity employee) {
        DBServiceIFC dbService = ServiceFactory.getDBService();
        dbService.executeSaveTask(new DBSaveTaskIFC() {
            @Override
            public Object save(DBConnectionIFC dbConnection) {
                try {
                    dbConnection.update(employee);
                    return null;
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    @Test
    void testDelete() {
        VersionEntity employee = generateEmployee();
        insertEmployee(employee);
        int numberBefore = getNumberOfEmployee();
        deleteEmployee(employee);
        int numberAfter = getNumberOfEmployee();
        assertEquals(1, numberBefore);
        assertEquals(0, numberAfter);
        System.out.println("testDelete passed");
    }

    private void deleteEmployee(VersionEntity employee) {
        DBServiceIFC dbService = ServiceFactory.getDBService();
        dbService.executeSaveTask(new DBSaveTaskIFC() {
            @Override
            public Object save(DBConnectionIFC dbConnection) {
                try {
                    dbConnection.delete(employee);
                    return null;
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    private int getNumberOfEmployee() {
        DBServiceIFC dbService = ServiceFactory.getDBService();
        Object result = dbService.executeQueryTask(new DBQueryTaskIFC() {
            @Override
            public Object query(DBConnectionIFC dbConnection) {
                try {
                    String sql = "select count(*) as number from employee";
                    return dbConnection.queryScalar(sql);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        return Integer.valueOf(result.toString());
    }

    @Test
    void testQueryScalar() {
        VersionEntity employee = generateEmployee();
        insertEmployee(employee);
        String name = getEmployeeName(employee.getId());
        assertEquals("Tom", name);
        System.out.println("testQueryScalar passed");
    }

    private String getEmployeeName(String employeeId) {
        DBServiceIFC dbService = ServiceFactory.getDBService();
        return (String) dbService.executeQueryTask(new DBQueryTaskIFC() {
            @Override
            public Object query(DBConnectionIFC dbConnection) {
                try {
                    String sql = "select name from employee where id = ?";
                    List<Object> params = new ArrayList<Object>();
                    params.add(employeeId);
                    SQLStruct sqlStruct = new SQLStruct(sql, params);
                    return dbConnection.queryScalar(sqlStruct);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    @Test
    void testQuery() {
        VersionEntity employee = generateEmployee();
        insertEmployee(employee);
        List<Map<String, Object>> result = queryBySQL();
        assertTrue(result.size() > 0);
        System.out.println("testQuery passed");
    }

    private List<Map<String, Object>> queryBySQL() {
        DBServiceIFC dbService = ServiceFactory.getDBService();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rows =
            (List<Map<String, Object>>) dbService.executeQueryTask(new DBQueryTaskIFC() {
                @Override
                public Object query(DBConnectionIFC dbConnection) {
                    try {
                        String sql = "select * from employee";
                        return dbConnection.query(sql);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
        return rows;
    }

    @Test
    void testLoadById() {
        VersionEntity employee = generateEmployee();
        insertEmployee(employee);
        VersionEntity versionEntity = loadById(employee.getId());
        System.out.println("address = " + versionEntity.getAttribute("address"));
        assertEquals("Tom", versionEntity.getAttribute("name"));
        System.out.println("testLoadById passed");
    }

    private VersionEntity loadById(String employeeId) {
        DBServiceIFC dbService = ServiceFactory.getDBService();
        return (VersionEntity) dbService.executeQueryTask(new DBQueryTaskIFC() {
            @Override
            public Object query(DBConnectionIFC dbConnection) {
                try {
                    return dbConnection.loadVersionEntityById("employee", employeeId);
                } catch (SQLException ex) {
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
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}

