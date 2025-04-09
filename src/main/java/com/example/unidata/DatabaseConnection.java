package com.example.unidata;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    public static Connection connectDb() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection connect = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521/xepdb1", "DBA_MANAGER", "123");
            return connect;
        }catch(Exception e) {e.printStackTrace();}
        return null;
    }
}
