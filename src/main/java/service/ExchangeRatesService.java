package service;

import DAO.CurrencyDAO;
import DAO.ExchangeRatesDAO;
import model.Currency;
import model.ExchangeRate;

import java.sql.SQLException;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import service.CurrencyService.CurrencyNotFoundException;
public class ExchangeRatesService {
    private ExchangeRatesDAO _dao = new ExchangeRatesDAO();
    private CurrencyDAO _cdao = new CurrencyDAO();


    public List<ExchangeRate> getAllExchangeRates(){
        List<ExchangeRate> rates = _dao.getAllExchangeRates();
        return rates;
    }

    public ExchangeRate addNewExchangeRate(String baseCurrencyCode,
                                           String targetCurrencyCode, double rate) throws SQLException {
        boolean isFoundBase = _cdao.findCurrency(baseCurrencyCode);
        if(!isFoundBase){
            throw new CurrencyNotFoundException("Базовая валюта не найдена в бд");
        }

        boolean isFoundTarget = _cdao.findCurrency(targetCurrencyCode);
        if(!isFoundTarget){
            throw new CurrencyNotFoundException("Целевая валюта не найдена в бд");
        }

        Currency baseCurrency = _cdao.getCurrencyByCode(baseCurrencyCode);
        int baseCurrencyID = baseCurrency.get_id();

        Currency targetCurrency = _cdao.getCurrencyByCode(targetCurrencyCode);
        int targetCurrencyID = targetCurrency.get_id();

        boolean isFoundPair = _dao.findExchangeRatesPair(baseCurrencyID, targetCurrencyID);
        if(isFoundPair){
            throw new ExchangeRateAlreadyExistsException("Курс обмена уже существует");
        }

        int newRateID = _dao.addNewExchangeRate(baseCurrencyID,targetCurrencyID,rate);

        return new ExchangeRate(newRateID,baseCurrency,targetCurrency,rate);
    }


    public ExchangeRate findExchangeRatePairByCode(String baseCode, String targetCode) throws SQLException {
        boolean isFoundBase = _cdao.findCurrency(baseCode);
        if(!isFoundBase){
            throw new CurrencyNotFoundException("Базовая валюта не найдена в бд");
        }

        boolean isFoundTarget = _cdao.findCurrency(targetCode);
        if(!isFoundTarget){
            throw new CurrencyNotFoundException("Целевая валюта не найдена в бд");
        }

        Currency baseCurrency = _cdao.getCurrencyByCode(baseCode);
        int baseCurrencyID = baseCurrency.get_id();

        Currency targetCurrency = _cdao.getCurrencyByCode(targetCode);
        int targetCurrencyID = targetCurrency.get_id();

        boolean isFoundPair = _dao.findExchangeRatesPair(baseCurrencyID, targetCurrencyID);
        if(!isFoundPair){
            throw new ExchangeRateNotFoundException("Курс обмена не найден");
        }

        ExchangeRate rate = _dao.receiveExchangeRatePair(baseCurrency,targetCurrency);

        return rate;
    }


    public static class ExchangeRateAlreadyExistsException extends RuntimeException{
        public ExchangeRateAlreadyExistsException(String message){
            super(message);
        }
    }

    public static class ExchangeRateNotFoundException extends RuntimeException {
        public ExchangeRateNotFoundException(String message) {
            super(message);
        }
    }
}
