package org.neo.servaframe.interfaces;

/***
 * the implementation of this task will
 * do some action to save in DB
 * 
 */
public interface DBSaveTaskIFC extends TaskIFC {
    /*
     * this is a callback method, the implementation of it
     * execute the real save task, the framework will call
     * back this method in runtime.
     *
     * param session     it is provided by frameword as an interface
     *                   to visit DB
     * return
     */

    public Object save(DBConnectionIFC dbConnection);
}
