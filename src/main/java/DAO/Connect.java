package DAO;
import java.io.Console;
import java.sql.*;
import java.util.List;

 class Connect {
     private static String url;
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


