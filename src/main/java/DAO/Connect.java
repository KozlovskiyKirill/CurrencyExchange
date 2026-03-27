package DAO;
import java.io.Console;
import java.sql.*;
import java.util.List;

 class Connect {
     private static String url;

     static {
         try {
             Class<?> clazz = Class.forName("org.sqlite.JDBC");
             Driver driver = (Driver) clazz.getDeclaredConstructor().newInstance();
             DriverManager.registerDriver(driver);
             System.out.println("SQLITE DRIVER REGISTERED");
         } catch (Exception e) {
             throw new RuntimeException("Failed to register SQLite driver", e);
         }
     }
     static void init(String dbAbsolutePath) {
         url = "jdbc:sqlite:" + dbAbsolutePath;
         System.out.println("SQLITE URL = " + url);
     }

     public static Connection getConnect() throws SQLException {
         if (url == null) {
             throw new IllegalStateException("Database is not initialized");
         }

         Connection connection = DriverManager.getConnection(url);
         try (Statement st = connection.createStatement()) {
             st.execute("PRAGMA foreign_keys = ON");
         }
         return connection;
     }
 }


