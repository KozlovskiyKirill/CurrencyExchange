package DAO;

import model.Currency;
import model.ExchangeRate;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExchangeRatesDAO {

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

    public boolean findExchangeRatesPair(int baseCurrencyID, int targetCurrencyID)throws SQLException {
        try(Connection connection = Connect.getConnect()){
            PreparedStatement statement = connection.prepareStatement("select * from exchange_rates where " +
                    "BaseCurrencyID=? and TargetCurrencyID=?");
            statement.setString(1,String.valueOf(baseCurrencyID));
            statement.setString(2,String.valueOf(targetCurrencyID));
            ResultSet set = statement.executeQuery();
            return set.next();
        }
    }

    public int addNewExchangeRate(int baseCurrencyID, int targetCurrencyID, double rate)throws SQLException{
        try(Connection connection = Connect.getConnect()){
            PreparedStatement statement = connection.prepareStatement("insert into exchange_rates(BaseCurrencyID," +
                    "TargetCurrencyID,Rate) values(?,?,?)",Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1,baseCurrencyID);
            statement.setInt(2,targetCurrencyID);
            statement.setDouble(3,rate);
            int rows = statement.executeUpdate();
            if(rows ==1) {
                ResultSet rs = statement.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    return id;
                }
                else throw new RuntimeException("не забрал id");
            }
            else throw new RuntimeException("не смог добавить");
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
                double rate = set.getDouble(4);
                newRate =  new ExchangeRate(id,baseCurrency,targetCurrency,rate);
            }
            return newRate;
        }
    }

}
