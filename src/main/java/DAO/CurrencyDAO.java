package DAO;
import model.Currency;
import model.ExchangeRate;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CurrencyDAO {


   public List<Currency> getAllCurrencies() throws SQLException {

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
               Currency cur = new Currency(id, fullname,code,sign);
               currencies.add(cur);
           }
           return Collections.unmodifiableList(currencies);

       }
   }
   public boolean findCurrency(String code)throws SQLException{
       try(Connection con = Connect.getConnect()){
           PreparedStatement statement = con.prepareStatement("SELECT * FROM currencies WHERE Code = ?");
           statement.setString(1, code);
           ResultSet rs = statement.executeQuery();
           return rs.next();
       }
   }

   public Currency addNewCurrency(String name,String code,String sign) throws SQLException{
       // сделать проверки (выбрпсывать ошибки)
       try(Connection con = Connect.getConnect()){
           PreparedStatement statement =
                   con.prepareStatement("INSERT INTO currencies (Code,FullName,Sign) VALUES (?,?,?)",
                           Statement.RETURN_GENERATED_KEYS);
           statement.setString(1,code);
           statement.setString(2,name);
           statement.setString(3,sign);
           int rows = statement.executeUpdate();
           if(rows ==1) {
               ResultSet rs = statement.getGeneratedKeys();
               if (rs.next()) {
                   int id = rs.getInt(1);
                    return new Currency(id,name,code,sign);
               }
               else throw new RuntimeException("не забрал id");
           }
           else throw new RuntimeException("не смог добавить");

       }

   }

}
