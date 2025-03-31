package org.example.ctrl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;

public class ConnectionFactory {
    private static final String KEY_DB_URL = "DB_URL", KEY_DB_DRIVER = "DB_DRIVER", KEY_DB_USER = "DB_USER", KEY_DB_PWD = "DB_PWD";
    private static final String[] KEYS = new String[]{KEY_DB_URL, KEY_DB_DRIVER, KEY_DB_USER, KEY_DB_PWD};
    private static final String DEF_DB_URL = "jdbc:oracle:thin:@tcif.htl-villach.at:1521/orcl", DEF_DB_DRIVER = "oracle.jdbc.OracleDriver", DEF_DB_USER = "d3b13", DEF_DB_PWD = "d3b13";
    private static final String[] DEFS = new String[]{DEF_DB_URL, DEF_DB_DRIVER, DEF_DB_USER, DEF_DB_PWD};

    private static final ConnectionFactory instance = new ConnectionFactory();

    private Connection dbConn;
    private final Properties dbConfig = getProperties();
    private String getProperty(String key) {
        return dbConfig.getProperty(key);
    }
    private ConnectionFactory() {
        System.out.println("ConnectionFactory init");
        try {
            Class.forName(dbConfig.getProperty(KEY_DB_DRIVER));
        } catch (ClassNotFoundException e) {
            System.err.println("Could not find driver, defaulting to Oracle...");
            try {
                Class.forName(DEF_DB_DRIVER);
            } catch (ClassNotFoundException e2) {
                System.err.println("Could not find Oracle Driver!");
            }
        }

        try {
            dbConn = DriverManager.getConnection(getProperty(KEY_DB_URL), getProperty(KEY_DB_USER), getProperty(KEY_DB_PWD));
        } catch (SQLException e) {
            System.err.println("Could not make connection: " + e);
        }
    }

    public Statement createStatement() throws SQLException {
        return dbConn.createStatement();
    }
    public Connection getConnection() {
        return dbConn;
    }
    public String[] getTables() throws SQLException {
        ResultSet rs = dbConn.getMetaData().getTables(dbConn.getCatalog(), dbConn.getSchema(), null, null);
        ArrayList<String> ret = new ArrayList<>();
        while (rs.next()) {
            ret.add(rs.getString("TABLE_NAME"));
        }
        String[] tmp = new String[0];
        return ret.toArray(tmp);
    }

    @Override
    protected void finalize() throws Throwable {
        dbConn.close();
        super.finalize();
    }

    private static String concatPath(String dir) {
        return dir.concat(System.getProperty("file.separator")).concat("db.properties");
    }
    private static Properties getProperties() {
        FileInputStream propertyFile = null;
        try {
            propertyFile = new FileInputStream(concatPath(System.getProperty("user.home")));
        } catch (FileNotFoundException ignored) {
            try {
                propertyFile = new FileInputStream(concatPath(System.getenv("app.home")));
            } catch (FileNotFoundException | NullPointerException ignored2) {
                try {
                    propertyFile = new FileInputStream(concatPath(System.getProperty("user.dir")));
                } catch (FileNotFoundException ignored1) {
                }
            }
        }
        Properties p = new Properties();
        if (propertyFile != null) {
            try {
                p.load(propertyFile);
            } catch (IOException ignored) {}
        }

        boolean writeNew = false;
        Iterator<String> keyIt = Arrays.stream(KEYS).iterator();
        while (keyIt.hasNext() && !writeNew) {
            String key = keyIt.next();;
            if (p.getProperty(key) == null) {
                writeNew = true;
            }
        }
        if (writeNew) {
            p = new Properties();
            for (int i = 0; i < KEYS.length; i++) {
                p.setProperty(KEYS[i], DEFS[i]);
            }
            try {
                p.store(new FileOutputStream(concatPath(System.getProperty("user.dir"))), "Database Config");
            } catch (IOException e) {
                System.err.println("property file could not be written: " + e);
            }
        }

        return p;
    }

    public static ConnectionFactory getInstance() {return instance;}
}
