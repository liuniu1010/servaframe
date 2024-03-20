package org.neo.servaframe.interfaces;

/***
 * the implementation of this task should execute 
 * a query task without saving action
 * 
 */
public interface DBQueryTaskIFC extends TaskIFC {
    /*
     * this is a callback method, the implementation of it
     * execute the real query task, the framework will call
     * back this method in runtime.
     *
     * param dbConnection     it is provided by framework as an interface
     *                        to visit DB
     * return Object
     */
    public Object query(DBConnectionIFC dbConnection);
}
