package DAO;
import jdk.internal.util.xml.impl.Pair;
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

   public List<ExchangeRate> getAllExchangeRates(){
       try(Connection connection = Connect.getConnect()){
           List<ExchangeRate> rates = new ArrayList<>();
           Statement statement = connection.createStatement();
           ResultSet rs = statement.executeQuery("select\n" +
                   "# id записи курса обмена\n" +
                   "er.id,\n" +
                   "# базовая валюта\n" +
                   "bc.ID,\n" +
                   "bc.FullName,\n" +
                   "bc.code,\n" +
                   "bc.Sign,\n" +
                   "# целевая валюта\n" +
                   "tc.ID,\n" +
                   "tc.FullName,\n" +
                   "tc.code,\n" +
                   "tc.Sign,\n" +
                   "# курс\n" +
                   "er.rate\n" +
                   "\n" +
                   "from exchange_rates er\n" +
                   "join currencies bc on er.BaseCurrencyID=bc.ID\n" +
                   "join currencies tc on er.TargetCurrencyID=tc.ID;");
           while(rs.next()){

               /**ПРОВЕРКА ОРГАНИЗОВАТЬ!!!!!!!!!!*/
               int id = rs.getInt(1);
               // получение данных базовой валюты
               int b_id = rs.getInt(2);
               String b_name = rs.getString(3);
               String b_code = rs.getString(4);
               String b_sign = rs.getString(5);
               Currency baseCurrency = new Currency(b_id,b_name,b_code,b_sign);
               // получение данных целевой валюты
               int t_id = rs.getInt(6);
               String t_name = rs.getString(7);
               String t_code = rs.getString(8);
               String t_sign = rs.getString(9);
               Currency targetCurrency = new Currency(t_id,t_name,t_code,t_sign);
               double rate = rs.getDouble(10);
               ExchangeRate exchangeRate = new ExchangeRate(id,baseCurrency,targetCurrency,rate);
               rates.add(exchangeRate);
           }
           return Collections.unmodifiableList(rates);

       } catch (SQLException e) {
           throw new RuntimeException(e);
       }
   }

}
