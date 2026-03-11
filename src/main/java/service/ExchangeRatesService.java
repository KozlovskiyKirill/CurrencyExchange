package service;

import DAO.CurrencyDAO;
import model.ExchangeRate;

import java.sql.SQLException;
import org.apache.commons.lang3.tuple.Pair;
import java.util.Currency;
import java.util.List;
public class ExchangeRatesService {
    private CurrencyDAO _dao = new CurrencyDAO();



    public List<ExchangeRate> getAllExchangeRates(){
        List<ExchangeRate> rates = _dao.getAllExchangeRates();
        return rates;
    }

}
