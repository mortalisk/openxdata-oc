package org.openxdata.server.export.rdbms.engine;

import java.io.Serializable;

public class TableQuery implements Serializable {

    private static final long serialVersionUID = -1543351773322897605L;

    private final String tableName;
    private final String sql;
    
    public TableQuery(String tableName, String sql) {
        this.tableName = tableName;
        this.sql = sql;
    }

    public String getTableName() {
        return tableName;
    }

    public String getSql() {
        return sql;
    }

}
