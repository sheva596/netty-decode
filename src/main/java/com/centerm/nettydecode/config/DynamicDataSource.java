package com.centerm.nettydecode.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @author Sheva
 * @date 2020/4/26 13:40
 * @description
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    public enum DatabaseType {
        primaryDataSource,
        readonlyDataSource
    }

    private static final ThreadLocal<DatabaseType> contextHolder = new ThreadLocal<>();

    public static void setDataBaseType(DatabaseType type) {
        contextHolder.set(type);
    }

    public static DatabaseType getDataBaseType() {
        DatabaseType db = contextHolder.get();
        if (db == null) {
            db = DatabaseType.primaryDataSource;
        }
        return db;
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return getDataBaseType();
    }

    /**
     * 清理链接类型
     */
    public static void clearDbType() {
        contextHolder.remove();
    }
}
