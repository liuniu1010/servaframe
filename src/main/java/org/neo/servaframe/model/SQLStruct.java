package org.neo.servaframe.model;

import java.util.List;

public class SQLStruct {
    private String SQL;
    private List<Object> params;

    public SQLStruct(String inputSQL, List<Object> inputParams) {
        SQL = inputSQL;
        params = inputParams;
    }

    public String getSQL() {
        return SQL;
    }

    public List<Object> getParams() {
        return params;
    }

    @Override
    public String toString() {
        String str = SQL;
        if(params == null) {
            return str;
        }

        int index = 1;
        for(Object paramValue: params) {
            str += "\nparam " + index + " = " + paramValue;
            index++;
        }
        return str;
    }
}
