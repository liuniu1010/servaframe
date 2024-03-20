package org.neo.servaframe.util;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;

import org.apache.log4j.Logger;

/***
 * utility class provides methods to visist configuration files
 *
 */
public class ConfigUtil {
    final static Logger logger = Logger.getLogger(ConfigUtil.class);

    private final static String DB_CONFIG_FILE = "database.conf";
    private final static String DB_JDBC_DRIVER = "jdbcDriver";
    private final static String DB_URL = "dbUrl";
    private final static String DB_USERNAME = "username";
    private final static String DB_PASSWORD = "password";

    public static String getJdbcDriver() throws IOException, FileNotFoundException {
        return getDbConfigCache().get(DB_JDBC_DRIVER);
    }

    public static String getDbUrl() throws IOException, FileNotFoundException {
        return getDbConfigCache().get(DB_URL);
    }

    public static String getDbUsername() throws IOException, FileNotFoundException {
        return getDbConfigCache().get(DB_USERNAME);
    }

    public static String getDbPassword() throws IOException, FileNotFoundException {
        return getDbConfigCache().get(DB_PASSWORD);
    }


    private final static String SERVICE_CONFIG_FILE = "service.conf";
    private final static String SERVICE_DBSERVICE = "DBService";

    public static String getDBServiceClassname() throws IOException, FileNotFoundException {
        return getServiceConfigCache().get(SERVICE_DBSERVICE);
    }

    private static void clearConfigCache() {
        dbConfigList = null;
        dbConfigCache = null;
        serviceConfigList = null;
        serviceConfigCache = null;
    }

    private static List<String> serviceConfigList = null;
    private static List<String> getServiceConfigList()  throws IOException, FileNotFoundException {
        if(serviceConfigList == null) {
            serviceConfigList = getTextFileContent(SERVICE_CONFIG_FILE);
        }
        return serviceConfigList;
    }

    private static List<String> dbConfigList = null;
    private static List<String> getDbConfigList() throws IOException, FileNotFoundException {
        if(dbConfigList == null) {
            dbConfigList = getTextFileContent(DB_CONFIG_FILE);
        }
        return dbConfigList;
    }

    private static List<String> getTextFileContent(String fileName) throws IOException, FileNotFoundException {
        BufferedReader br = null;
        InputStream in = null;
        List<String> contentList = new ArrayList<String>();
        try {
            in = ConfigUtil.class.getClassLoader().getResourceAsStream(fileName);
            br = new BufferedReader(new InputStreamReader(in));
            String line = null;
            while((line = br.readLine()) != null) {
                contentList.add(line);
            }
        } 
        finally {
            IOUtil.closeBufferedReader(br);
        }
        return contentList;
    }

    private static Map<String, String> serviceConfigCache = null;
    private static Map<String, String> getServiceConfigCache() throws IOException, FileNotFoundException {
        if(serviceConfigCache != null) {
            return serviceConfigCache;
        }

        serviceConfigList = getServiceConfigList();
        serviceConfigCache = new HashMap<String, String>();
        for(String line: serviceConfigList) {
            String[] parts = line.split("=");
            if(parts.length == 2) {
                serviceConfigCache.put(parts[0].trim(), parts[1].trim());
            }
        }

        return serviceConfigCache;
    }

    private static Map<String, String> dbConfigCache = null;
    private static Map<String, String> getDbConfigCache()  throws IOException, FileNotFoundException {
        if(dbConfigCache != null) {
            return dbConfigCache;
        }

        dbConfigList = getDbConfigList();
        dbConfigCache = new HashMap<String, String>();
        for(String line: dbConfigList) {
            String[] parts = splitByFirstEquals(line);
            dbConfigCache.put(parts[0].trim(), parts[1].trim());
        }

        return dbConfigCache;
    }

    private static String[] splitByFirstEquals(String input) {
        int index = input.indexOf('=');
        if (index != -1) {
            String key = input.substring(0, index).trim();
            String value = input.substring(index + 1).trim();
            return new String[]{key, value};
        } 
        else {
            return new String[]{input.trim(), ""};
        }
    }
}
