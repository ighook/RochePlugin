package org.roche.roche.Database;

import java.sql.Connection;

public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection conn;

    public DatabaseManager(Connection conn) {
        this.conn = conn;
    }

    public static synchronized DatabaseManager getInstance(Connection conn) {
        if (instance == null) {
            instance = new DatabaseManager(conn);
        }
        return instance;
    }

    public Connection getConnection() {
        return conn;
    }


}
