package DAO;
import model.Currency;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CurrencyDAO {


   public List<Currency> getAll() throws SQLException {

       try (Connection con = Connect.getConnect())
       {

           System.out.print("Зашли в подключение");
           List<Currency> currencies = new ArrayList<>();
           Statement statement = con.createStatement();
           ResultSet results = statement.executeQuery("SELECT * FROM currencies");
           while(results.next()){
               int id = results.getInt(1);
               String code = results.getString(2);
               String fullname = results.getString(3);
               String sign = results.getString(4);
               Currency cur = new Currency(id,code,fullname,sign);
               currencies.add(cur);
           }
           return Collections.unmodifiableList(currencies);

       }
   }

}
