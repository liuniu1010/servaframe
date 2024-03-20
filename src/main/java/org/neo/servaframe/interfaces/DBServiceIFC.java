package org.neo.servaframe.interfaces;

/***
 * The interface which provide environment to execute Task
 * 
 * implementation of this interface should provide DBConnection
 * and ensure the transaction for each public methods
 *
 */
public interface DBServiceIFC extends ServiceIFC{
    /*
     * execute task to save(insert, update, delete) data into DB
     * the real action should be in in the task which implement
     * the interface DBSaveTaskIFC
     *
     * param saveTask   the task which do the save action
     * return           the returned object of saveTask.save() will be returned
     *                  in this method
     */
    public Object executeSaveTask(DBSaveTaskIFC saveTask);

    /*
     * execute query task(select) data from DB
     * the real select action should be in the task which 
     * implement the interface QueryTaskIFC
     *
     * param queryTask  the task which do the query action
     * return           the returned object of queryTask.query() will be returned
     *                  in this method
     */
    public Object executeQueryTask(DBQueryTaskIFC queryTask);
}
