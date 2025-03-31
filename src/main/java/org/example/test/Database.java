package org.example.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final String DEFAULT_URL = "jdbc:oracle:thin:@tcif.htl-villach.at:1521/orcl";
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        String driver = "oracle.jdbc.OracleDriver";
        Class.forName(driver);
        System.out.println("Driver loaded");
        Connection c = DriverManager.getConnection(DEFAULT_URL, "d3b13", "d3b13");
        System.out.println("Connected");
        c.close();
    }
}
