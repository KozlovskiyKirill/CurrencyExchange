package DAO;

import model.Currency;
import model.ExchangeRate;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExchangeRatesDAO {

    public List<ExchangeRate> getAllExchangeRates() throws SQLException {
        try(Connection connection = Connect.getConnect()){
            List<ExchangeRate> rates = new ArrayList<>();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select\n" +

                    "er.id,\n" +

                    "bc.ID,\n" +
                    "bc.FullName,\n" +
                    "bc.code,\n" +
                    "bc.Sign,\n" +

                    "tc.ID,\n" +
                    "tc.FullName,\n" +
                    "tc.code,\n" +
                    "tc.Sign,\n" +

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
                BigDecimal rate = rs.getBigDecimal(10);
                ExchangeRate exchangeRate = new ExchangeRate(id,baseCurrency,targetCurrency,rate);
                rates.add(exchangeRate);
            }
            return Collections.unmodifiableList(rates);
        }
    }

    public int findExchangeRatesPair(int baseCurrencyID, int targetCurrencyID)throws SQLException {
        try(Connection connection = Connect.getConnect()){
            PreparedStatement statement = connection.prepareStatement("select * from exchange_rates where " +
                    "BaseCurrencyID=? and TargetCurrencyID=?");
            statement.setString(1,String.valueOf(baseCurrencyID));
            statement.setString(2,String.valueOf(targetCurrencyID));
            ResultSet set = statement.executeQuery();
            if(set.next()){
                int id = set.getInt(1);
                return id;
            }
            return -1;
        }
    }

    public int addNewExchangeRate(int baseCurrencyID, int targetCurrencyID, BigDecimal rate)throws SQLException{
        try(Connection connection = Connect.getConnect()){
            PreparedStatement statement = connection.prepareStatement("insert into exchange_rates(BaseCurrencyID," +
                    "TargetCurrencyID,Rate) values(?,?,?)",Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1,baseCurrencyID);
            statement.setInt(2,targetCurrencyID);
            statement.setBigDecimal(3,rate);
            int rows = statement.executeUpdate();
            if(rows ==1) {
                ResultSet rs = statement.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    return id;
                }
                else throw new SQLException("Failed to fetch generated key for exchange rate");
            }
            else throw new SQLException("Failed to insert exchange rate");
        }
    }


    public ExchangeRate receiveExchangeRatePair(Currency baseCurrency, Currency targetCurrency) throws SQLException {
        try(Connection connection = Connect.getConnect()){
            PreparedStatement statement = connection.prepareStatement
                    ("select * from exchange_rates where BaseCurrencyID=? and TargetCurrencyID=?");
            statement.setInt(1,baseCurrency.get_id());
            statement.setInt(2,targetCurrency.get_id());
            ResultSet set = statement.executeQuery();
            ExchangeRate newRate = null;
            while (set.next()){
                int id = set.getInt(1);
                BigDecimal rate = set.getBigDecimal(4);
                newRate =  new ExchangeRate(id,baseCurrency,targetCurrency,rate);
            }
            return newRate;
        }
    }

    public ExchangeRate updateExchangeRatePair(Currency baseCurrency,Currency targetCurrency,int id, BigDecimal rate)
            throws SQLException{
        try(Connection connection = Connect.getConnect()){
            PreparedStatement statement = connection.prepareStatement
                    ("update exchange_rates set Rate=? where ID=?");
            statement.setBigDecimal(1,rate);
            statement.setInt(2,id);
            int updatedRows = statement.executeUpdate();

            if (updatedRows > 0) {
                return new ExchangeRate(id, baseCurrency, targetCurrency, rate);
            }

            return null;
        }
    }
}
