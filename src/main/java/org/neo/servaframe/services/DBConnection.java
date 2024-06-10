package org.neo.servaframe.services;

import java.util.Collection;
import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import java.util.concurrent.atomic.AtomicLong;

import org.neo.servaframe.interfaces.DBConnectionIFC;
import org.neo.servaframe.model.VersionEntity;
import org.neo.servaframe.model.SQLStruct;

/***
 * DBConnection which encapsulate the DB connection
 * drive SQL to visit DB at runtime
 *
 */
public class DBConnection implements DBConnectionIFC {
    final static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(DBConnection.class);

    private Connection conn = null;
    private boolean connValid = false;
    public DBConnection(Connection inputConn) {
        conn = inputConn;
        connValid = true;
    }

    public void close() {
        connValid = false;
        try {
            conn.close();
        }
        catch(RuntimeException rex) {
            throw rex;
        }
        catch(Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void checkValid() {
        if(!connValid) {
            throw new RuntimeException("the dbConnection is not valid!");
        }
    }

    @Override
    public boolean isValid() {
        return connValid;
    }

    @Override
    public void insert(VersionEntity versionEntity) throws SQLException{
        checkValid();
        doInsert(versionEntity);
    }

    private void doInsert(VersionEntity versionEntity) throws SQLException {
        // assign a unique id for this entity
        versionEntity.setId(assignNewId(versionEntity.getName()));

        // construct the sql part
        String SQL = "insert into " + versionEntity.getName() + "(";

        String fieldPart = "id, version";
        String valuePart = "?, ?";

        Set<String> attributeNames = versionEntity.getAttributeNames();
        List<String> attributeList = new ArrayList<String>();

        // add them into list to make sure
        // each time we get it out in the same order
        attributeList.addAll(attributeNames); 
        for(String attribute: attributeList) {
            fieldPart += ", " + attribute;
            valuePart += ", ?";
        }

        SQL += fieldPart + ") values (" + valuePart + ")";

        // construct params part for sqlstruct
        List<Object> params = new ArrayList<Object>();
        params.add(versionEntity.getId());
        params.add(1);

        for(String attribute: attributeList) {
            params.add(versionEntity.getAttribute(attribute));
        }

        this.doExecute(new SQLStruct(SQL, params));
    }

    @Override
    public void update(VersionEntity versionEntity) throws SQLException {
        checkValid();
        doUpdate(versionEntity);
    }

    private void doUpdate(VersionEntity versionEntity) throws SQLException {
        // construct the update sql
        String SQL = "update " + versionEntity.getName() + " set ";
        SQL += "version = version + 1";

        Set<String> attributeNames = versionEntity.getAttributeNames();
        List<String> attributeList = new ArrayList<String>();

        // add them into list to make sure
        // each time we get it out in the same order
        attributeList.addAll(attributeNames); 
        for(String attribute: attributeList) {
            SQL += ", " + attribute + " = ?";
        }

        SQL += " where id = " + versionEntity.getId();
        // SQL += " and version = " + versionEntity.getVersion();  // comment it out in case version control is not necessary

        List<Object> params = new ArrayList<Object>();

        for(String attribute: attributeList) {
            params.add(versionEntity.getAttribute(attribute));
        }

        this.doExecute(new SQLStruct(SQL, params));
    }

    @Override
    public void delete(VersionEntity versionEntity) throws SQLException {
        checkValid();
        doDelete(versionEntity);
    }

    private void doDelete(VersionEntity versionEntity) throws SQLException {
        // construct the SQL
        String SQL = "delete from " + versionEntity.getName();
        SQL += " where id = " + versionEntity.getId();
       
        this.doExecute(SQL); 
    }

    @Override
    public List<Map<String, Object>> query(String SQL) throws SQLException {
        checkValid();
        return doQuery(SQL);
    }

    @Override
    public List<VersionEntity> queryAsVersionEntity(String entityName, String SQL) throws SQLException {
        checkValid();
        List<Map<String, Object>> mapList = doQuery(SQL);
        return transMapListToVersionEntitys(entityName, mapList); 
    }

    @Override
    public List<Map<String, Object>> query(SQLStruct sqlStruct) throws SQLException {
        checkValid();
        return doQuery(sqlStruct);
    }

    @Override
    public List<VersionEntity> queryAsVersionEntity(String entityName, SQLStruct sqlStruct) throws SQLException {
        checkValid();
        List<Map<String, Object>> mapList = doQuery(sqlStruct);
        return transMapListToVersionEntitys(entityName, mapList); 
    }

    @Override
    public Object queryScalar(String SQL) throws SQLException {
        checkValid();
        return doQueryScalar(SQL);
    }

    @Override
    public Object queryScalar(SQLStruct sqlStruct) throws SQLException {
        checkValid();
        return doQueryScalar(sqlStruct);
    }

    private List<Map<String, Object>> doQuery(String SQL) throws SQLException {
        return doQuery(new SQLStruct(SQL, null));
    }

    private Object doQueryScalar(String SQL) throws SQLException {
        return doQueryScalar(new SQLStruct(SQL, null));
    }

    private Object doQueryScalar(SQLStruct sqlStruct) throws SQLException {
        List<Map<String, Object>> mapList = doQuery(sqlStruct);
        if(mapList == null || mapList.isEmpty()) {
            return null;
        }
        for(Map<String, Object> map: mapList) {
             Set<String> attributes = map.keySet();
             if(attributes== null || attributes.isEmpty()) {
                 return null;
             }
             for(String attribute: attributes) {
                 return map.get(attribute);
             }
        }

        return null;
    }

    private List<Map<String, Object>> doQuery(SQLStruct sqlStruct) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(sqlStruct.getSQL());
        List<Object> params = sqlStruct.getParams();
        if(params != null) {
            int index = 1;
            for(Object paramValue: params) {
                pstmt.setObject(index, paramValue);
                index++;
            }
        }

        logger.debug("execute query SQL: " + sqlStruct.toString());
        ResultSet rs = pstmt.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        while(rs.next()) {
            Map<String, Object> map = new HashMap<String, Object>();
            for(int i = 1;i <= columnCount;i++) {
                String columnName = rsmd.getColumnName(i);
                map.put(columnName, rs.getObject(columnName));
            }
            resultList.add(map);
        }

        return resultList;
    }

    @Override
    public void execute(String SQL) throws SQLException {
        checkValid();
        doExecute(SQL);
    }

    @Override
    public void execute(SQLStruct sqlStruct) throws SQLException {
        checkValid();
        doExecute(sqlStruct);
    }

    private void doExecute(String SQL) throws SQLException {
        doExecute(new SQLStruct(SQL, null));
    }

    private void doExecute(SQLStruct sqlStruct) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(sqlStruct.getSQL());
        List<Object> params = sqlStruct.getParams();
        if(params != null) {
            int index = 1;
            for(Object paramValue: params) {
                pstmt.setObject(index, paramValue);
                index++;
            }
        }

        logger.info("execute persist SQL: " + sqlStruct.toString());
        pstmt.executeUpdate();
    }

    @Override
    public VersionEntity loadVersionEntityById(String entityName, long id) throws SQLException {
        checkValid();
        return doLoadVersionEntityById(entityName, id);
    }

    private VersionEntity doLoadVersionEntityById(String name, long id) throws SQLException {
        // construct the SQL to load the entity
        String SQL = "select * from " + name;
        SQL += " where id = " + id;

        List<Map<String, Object>> resultList = this.doQuery(SQL);
        if(resultList == null || resultList.isEmpty()) {
            return null;
        }

        Map<String, Object> result = resultList.get(0);
        Set<String> attributes = result.keySet();
        VersionEntity versionEntity = transMapToVersionEntity(name, result);
        return versionEntity;
    }


    private static Map<String, AtomicLong> newIdMap = new HashMap<String, AtomicLong>();
    private long assignNewId(String entityName) throws SQLException {
        if(newIdMap.containsKey(entityName)) {
            AtomicLong newId = newIdMap.get(entityName);
            return newId.incrementAndGet();
        }

        String SQL = "select MAX(ID) AS MAXID from " + entityName;
        List<Map<String, Object>> resultList = this.doQuery(SQL);
        Long maxId = null;
        if(resultList == null || resultList.size() == 0) {
            // nosuch record, init newId as 0
            maxId = new Long(0);
        }
        else {
            Map<String, Object> resultMap = resultList.get(0);
            Object result = resultMap.get("MAXID");
            if(result == null) {
                maxId = new Long(0);
            }
            else {
                maxId = Long.valueOf(result.toString());
            }
        }

        synchronized(newIdMap) {
            /***
             * the reason to synchronized this block
             * is to prevent two thread from running into it 
             * and getting the same newId for the same entity
             */
            if(!newIdMap.containsKey(entityName)) {
                AtomicLong atomicId = new AtomicLong(maxId);
                newIdMap.put(entityName, atomicId);
            }
        }
        return assignNewId(entityName);
    }

    private VersionEntity transMapToVersionEntity(String entityName, Map<String, Object> map) {
        Set<String> attributes = map.keySet();
        VersionEntity versionEntity = new VersionEntity(entityName);
        for(String attribute: attributes) {
            if(attribute.equalsIgnoreCase("ID")) {
                versionEntity.setId(Long.valueOf(map.get(attribute).toString()));
            }
            else if(attribute.equalsIgnoreCase("VERSION")) {
                versionEntity.setVersion(Long.valueOf(map.get(attribute).toString()));
            }
            else {
                versionEntity.setAttribute(attribute.toLowerCase(), map.get(attribute));
            }
        }
        return versionEntity;
    }

    private List<VersionEntity> transMapListToVersionEntitys(String entityName, List<Map<String, Object>> mapList) {
        List<VersionEntity> versionEntitys = new ArrayList<VersionEntity>();
        for(Map<String, Object> map: mapList) {
            VersionEntity versionEntity = transMapToVersionEntity(entityName, map);
            versionEntitys.add(versionEntity);
        }

        return versionEntitys;
    }
}
