package org.roche.roche.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    private static final String URL = "jdbc:mysql://ighook.cafe24.com:3306/ighook";
    private static final String USER = "ighook";
    private static final String PASSWORD = "wlsqja4292!";
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";

    // 데이터베이스 연결
    public static Connection getConnection() {
        try {
            // JDBC 드라이버 로드
            Class.forName(DRIVER_CLASS);
            // 데이터베이스에 연결
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC 드라이버를 찾을 수 없습니다.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("데이터베이스 연결 실패:");
            e.printStackTrace();
        }
        return null;
    }

    // 데이터베이스 연결 해제
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.out.println("데이터베이스 연결을 닫는 중 오류가 발생했습니다.");
                e.printStackTrace();
            }
        }
    }
}
