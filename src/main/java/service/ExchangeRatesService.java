package service;

import DAO.CurrencyDAO;
import DAO.ExchangeRatesDAO;
import model.ExchangeRate;

import java.sql.SQLException;
import org.apache.commons.lang3.tuple.Pair;
import java.util.Currency;
import java.util.List;
public class ExchangeRatesService {
    private ExchangeRatesDAO _dao = new ExchangeRatesDAO();



    public List<ExchangeRate> getAllExchangeRates(){
        List<ExchangeRate> rates = _dao.getAllExchangeRates();
        return rates;
    }

}
