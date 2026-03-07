package DAO;
import java.io.Console;
import java.sql.*;
import java.util.List;

 class Connect {
    private final static String URL = "jdbc:mysql://localhost:3306/currency_exchange";
    private final static String LOGIN = "root";
    private final static String PASSWORD = "1682023";

     static {
         try {
             Class.forName("com.mysql.cj.jdbc.Driver");
         } catch (ClassNotFoundException e) {
             throw new RuntimeException("MySQL driver not found", e);
         }
     }

     static Connection getConnect() throws SQLException {
         return DriverManager.getConnection(URL, LOGIN, PASSWORD);
     }
 }


