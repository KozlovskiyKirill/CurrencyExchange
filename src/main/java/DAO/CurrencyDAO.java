package DAO;
import model.Currency;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CurrencyDAO {


   public List<Currency> getAllCurrencies() throws SQLException {

       try (Connection con = Connect.getConnect())
       {

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
               else throw new SQLException("Failed to fetch generated key for currency");
           }
           else throw new SQLException("Failed to insert currency");

       }

   }


   public Optional<Currency> getCurrencyByCode(String code) throws SQLException {
       try(Connection con = Connect.getConnect()){
           PreparedStatement statement = con.prepareStatement("SELECT * FROM currencies WHERE Code = ?");
           statement.setString(1,code);
           ResultSet rs = statement.executeQuery();
           if(rs.next()){
               int _id = rs.getInt(1);
               String _name = rs.getString(2);
               String _code = rs.getString(3);
               String _sign = rs.getString(4);
               Currency cur = new Currency(_id, _name,code,_sign);
               return Optional.of(cur);
           }
           return Optional.empty();
       }
   }

}
