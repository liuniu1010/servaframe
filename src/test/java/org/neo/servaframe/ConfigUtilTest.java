package org.neo.servaframe;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.neo.servaframe.util.*;

/**
 * Unit test for simple App.
 */
public class ConfigUtilTest 
    extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ConfigUtilTest( String testName ) {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite( ConfigUtilTest.class );
    }

    public void testGetConfigs() throws Exception {
        String jdbcDriver = ConfigUtil.getJdbcDriver();
        String dbUrl = ConfigUtil.getDbUrl();
        String dbUsername = ConfigUtil.getDbUsername();
        String dbPassword = ConfigUtil.getDbPassword();
        String dbServiceClassname = ConfigUtil.getDBServiceClassname();
        System.out.println("jdbcDriver = " + jdbcDriver);
        System.out.println("dbUrl = " + dbUrl);
        System.out.println("dbUsername = " + dbUsername);
        System.out.println("dbPassword = " + dbPassword);
        System.out.println("dbServiceClassname = " + dbServiceClassname);
    }
}
