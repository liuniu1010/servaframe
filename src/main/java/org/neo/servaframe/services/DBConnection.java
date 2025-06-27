package org.neo.servaframe.services;

import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.UUID;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;


import org.neo.servaframe.interfaces.DBConnectionIFC;
import org.neo.servaframe.model.VersionEntity;
import org.neo.servaframe.model.SQLStruct;
import org.neo.servaframe.model.NeoConcurrentHashMap;

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

        SQL += " where id = '" + versionEntity.getId() + "'";
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
        SQL += " where id = '" + versionEntity.getId() + "'";
       
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
    public VersionEntity querySingleAsVersionEntity(String entityName, String SQL) throws SQLException {
        checkValid();
        Map<String, Object> result = this.doSingleQuery(SQL);
        if(result == null) {
            return null;
        }

        Set<String> attributes = result.keySet();
        VersionEntity versionEntity = transMapToVersionEntity(entityName, result);
        return versionEntity;
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
    public VersionEntity querySingleAsVersionEntity(String entityName, SQLStruct sqlStruct) throws SQLException {
        checkValid();
        Map<String, Object> result = this.doSingleQuery(sqlStruct);
        if(result == null) {
            return null;
        }

        Set<String> attributes = result.keySet();
        VersionEntity versionEntity = transMapToVersionEntity(entityName, result);
        return versionEntity;
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

    private Map<String, Object> doSingleQuery(String SQL) throws SQLException {
        return doSingleQuery(new SQLStruct(SQL, null));
    }

    private Object doQueryScalar(String SQL) throws SQLException {
        return doQueryScalar(new SQLStruct(SQL, null));
    }

    private Object doQueryScalar(SQLStruct sqlStruct) throws SQLException {
        Map<String, Object> map = doSingleQuery(sqlStruct);
        if(map == null) {
            return null;
        }

        Set<String> attributes = map.keySet();
        if(attributes== null || attributes.isEmpty()) {
            return null;
        }
        for(String attribute: attributes) {
            return map.containsKey(attribute)?map.get(attribute):null;
        }
        return null;
    }

    private PreparedStatement generateStatement(SQLStruct sqlStruct) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(sqlStruct.getSQL());
        List<Object> params = sqlStruct.getParams();
        if(params != null) {
            int index = 1;
            for(Object paramValue: params) {
                pstmt.setObject(index, paramValue);
                index++;
            }
        }

        return pstmt;
    }

    private List<Map<String, Object>> doQuery(SQLStruct sqlStruct) throws SQLException {
        PreparedStatement pstmt = generateStatement(sqlStruct);

        logger.debug("execute query SQL: " + sqlStruct.toString());
        ResultSet rs = pstmt.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        while(rs.next()) {
            Map<String, Object> map = new NeoConcurrentHashMap<String, Object>();
            for(int i = 1;i <= columnCount;i++) {
                String columnLabel = rsmd.getColumnLabel(i);
                Object oValue = rs.getObject(columnLabel);
                map.put(columnLabel, oValue);
            }
            resultList.add(map);
        }

        return resultList;
    }

    private Map<String, Object> doSingleQuery(SQLStruct sqlStruct) throws SQLException {
        PreparedStatement pstmt = generateStatement(sqlStruct);

        logger.debug("execute query SQL: " + sqlStruct.toString());
        ResultSet rs = pstmt.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
       
        Map<String, Object> map = null; 
        if(rs.next()) {
            map = new NeoConcurrentHashMap<String, Object>();
            for(int i = 1;i <= columnCount;i++) {
                String columnLabel = rsmd.getColumnLabel(i);
                Object oValue = rs.getObject(columnLabel);
                map.put(columnLabel, oValue);
            }
        }

        return map;
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
        PreparedStatement pstmt = generateStatement(sqlStruct);

        logger.info("execute persist SQL: " + sqlStruct.toString());
        pstmt.executeUpdate();
    }

    @Override
    public VersionEntity loadVersionEntityById(String entityName, String id) throws SQLException {
        checkValid();
        return doLoadVersionEntityById(entityName, id);
    }

    private VersionEntity doLoadVersionEntityById(String name, String id) throws SQLException {
        // construct the SQL to load the entity
        String SQL = "select * from " + name;
        SQL += " where id = '" + id + "'";

        Map<String, Object> result = this.doSingleQuery(SQL);
        if(result == null) {
            return null;
        }

        Set<String> attributes = result.keySet();
        VersionEntity versionEntity = transMapToVersionEntity(name, result);
        return versionEntity;
    }


    private String assignNewId(String entityName) throws SQLException {
        return UUID.randomUUID().toString();
    }

    private VersionEntity transMapToVersionEntity(String entityName, Map<String, Object> map) {
        Set<String> attributes = map.keySet();
        VersionEntity versionEntity = new VersionEntity(entityName);
        for(String attribute: attributes) {
            if(attribute.equalsIgnoreCase("ID")) {
                versionEntity.setId(map.get(attribute).toString());
            }
            else if(attribute.equalsIgnoreCase("VERSION")) {
                versionEntity.setVersion(Long.valueOf(map.get(attribute).toString()));
            }
            else {
                versionEntity.setAttribute(attribute.toLowerCase(), map.containsKey(attribute)?map.get(attribute):null);
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
