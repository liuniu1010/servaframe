package org.neo.servaframe;

import org.neo.servaframe.util.ConfigUtil;
import org.neo.servaframe.interfaces.DBServiceIFC;

/***
 * factory to get service instance
 *
 */
public class ServiceFactory {
    private static DBServiceIFC dbService = null;
    public static DBServiceIFC getDBService() {
        if(dbService == null) {
            try{
                String dbServiceClassname = ConfigUtil.getDBServiceClassname();
                dbService = (DBServiceIFC)Class.forName(dbServiceClassname).newInstance();
                dbService.init();
            }
            catch(Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        return dbService;
    }
}
