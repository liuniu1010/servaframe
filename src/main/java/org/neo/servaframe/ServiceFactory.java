package org.neo.servaframe;

import org.neo.servaframe.util.ConfigUtil;
import org.neo.servaframe.interfaces.DBServiceIFC;

/***
 * factory to get service instance
 */
public class ServiceFactory {
    private static DBServiceIFC dbService = null;

    public static DBServiceIFC getDBService() {
        if (dbService == null) {
            try {
                String dbServiceClassname = ConfigUtil.getDBServiceClassname();
                Class<?> clazz = Class.forName(dbServiceClassname);
                dbService = (DBServiceIFC) clazz.getDeclaredConstructor().newInstance();
                dbService.init();
            } 
            catch (Exception ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
        }
        return dbService;
    }
}

