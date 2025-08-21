package org.neo.servaframe;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.neo.servaframe.util.ConfigUtil;

class ConfigUtilTest {

    @Test
    void testGetConfigs() {
        String jdbcDriver = assertDoesNotThrow(ConfigUtil::getJdbcDriver);
        String dbUrl = assertDoesNotThrow(ConfigUtil::getDbUrl);
        String dbUsername = assertDoesNotThrow(ConfigUtil::getDbUsername);
        String dbPassword = assertDoesNotThrow(ConfigUtil::getDbPassword);
        String dbServiceClassname = assertDoesNotThrow(ConfigUtil::getDBServiceClassname);

        System.out.println("jdbcDriver = " + jdbcDriver);
        System.out.println("dbUrl = " + dbUrl);
        System.out.println("dbUsername = " + dbUsername);
        System.out.println("dbPassword = " + dbPassword);
        System.out.println("dbServiceClassname = " + dbServiceClassname);
    }
}

