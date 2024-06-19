package org.neo.servaframe.interfaces;

import java.util.Collection;
import java.util.Map;
import java.util.List;

import java.sql.SQLException;

import org.neo.servaframe.model.VersionEntity;
import org.neo.servaframe.model.SQLStruct;

/***
 * this interface encapsulate the DB connection, provide the
 * methods to visit DB
 *
 */
public interface DBConnectionIFC extends ConnectionIFC {
    /*
     * insert the versionEntity as a record in DB. the insert SQL will be automatedly
     * driven and executed.
     * 
     * param versionEntity    the object which be inserted
     * return
     */
    public void insert(VersionEntity versionEntity) throws SQLException;

    /*
     * update the versionEntity as a record in DB. the update SQL will be automatedly
     * driven and executed.
     * 
     * param versionEntity    the object which be update
     * return
     */
    public void update(VersionEntity versionEntity) throws SQLException;

    /*
     * delete the versionEntity from DB. the delete SQL will be automatedly
     * driven and executed.
     * 
     * param versionEntity    the object which be delete
     * return
     */
    public void delete(VersionEntity versionEntity) throws SQLException;

    /*
     * execute query by the provided SQL
     *
     * param SQL       the SQL to be executed
     * param return    query result will be contructed in List<Map<String, Object>>
     *                 each record is an elment of the List
     *                 for each record, each column name and related value will be 
     *                 set into Map<String, Object>
     */
    public List<Map<String, Object>> query(String SQL) throws SQLException;

    /*
     * execute query by the provided SQL
     *
     * param SQL       the SQL to be executed
     * param return    query result will be contructed as List<VersionEntity>
     */
    public List<VersionEntity> queryAsVersionEntity(String entityName, String SQL) throws SQLException;

    /*
     * execute query by the provided SQL
     *
     * param SQL       the SQL to be executed
     * param return    only first result will be contructed as VersionEntity
     */
    public VersionEntity querySingleAsVersionEntity(String entityName, String SQL) throws SQLException;

    /*
     * execute query by the provided SQLStruct
     *
     * param sqlStruct the SQLStruct to be executed
     * param return    query result will be contructed in List<Map<String, Object>>
     *                 each record is an elment of the List
     *                 for each record, each column name and related value will be 
     *                 set into Map<String, Object>
     */
    public List<Map<String, Object>> query(SQLStruct sqlStruct) throws SQLException;

    /*
     * execute query by the provided SQL
     *
     * param SQL       the SQL to be executed
     * param return    query result will be contructed as List<VersionEntity>
     */
    public List<VersionEntity> queryAsVersionEntity(String entityName, SQLStruct sqlStruct) throws SQLException;

    /*
     * execute query by the provided SQL
     *
     * param SQL       the SQL to be executed
     * param return    only first result will be contructed as VersionEntity
     */
    public VersionEntity querySingleAsVersionEntity(String entityName, SQLStruct sqlStruct) throws SQLException;

    /*
     * execute query by the provided SQL
     *
     * param SQL       the SQL to be executed
     * param return    only return one value
     */
    public Object queryScalar(String SQL) throws SQLException;

    /*
     * execute query by the provided SQLStruct
     *
     * param sqlStruct the SQLStruct to be executed
     * param return    only return one value
     */
    public Object queryScalar(SQLStruct sqlStruct) throws SQLException;

    /*
     * execute SQL directly
     * param SQL       the SQL to be executed
     */
    public void execute(String SQL) throws SQLException;

    /*
     * execute SQLStruct directly
     * param sqlStruct the SQLStruct to be executed
     */
    public void execute(SQLStruct sqlStruct) throws SQLException;

    /*
     * query VersionEntity by id
     *
     * param entityName
     * param id
     * return VersionEntity
     */
    public VersionEntity loadVersionEntityById(String entityName, long id) throws SQLException;

    /*
     * return if current connection is valid
     *
     */ 
    public boolean isValid();


    /*
     * close the connection
     */ 
    public void close();
}
