package service;

import DAO.CurrencyDAO;
import DAO.ExchangeRatesDAO;
import model.Currency;
import model.ExchangeRate;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Collections;
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
        boolean CurrenciesAreFound = CurrenciesAreFound(baseCurrencyCode,targetCurrencyCode);
        if(!CurrenciesAreFound){
            throw new CurrencyNotFoundException("Не найдена одна из валют");
        }

        Currency baseCurrency = _cdao.getCurrencyByCode(baseCurrencyCode);
        int baseCurrencyID = baseCurrency.get_id();

        Currency targetCurrency = _cdao.getCurrencyByCode(targetCurrencyCode);
        int targetCurrencyID = targetCurrency.get_id();

        int isFoundPair = _dao.findExchangeRatesPair(baseCurrencyID, targetCurrencyID);
        if(isFoundPair!=-1){
            throw new ExchangeRateAlreadyExistsException("Курс обмена уже существует");
        }

        int newRateID = _dao.addNewExchangeRate(baseCurrencyID,targetCurrencyID,rate);

        return new ExchangeRate(newRateID,baseCurrency,targetCurrency,rate);
    }


    public ExchangeRate findExchangeRatePairByCode(String baseCode, String targetCode) throws SQLException {
        boolean CurrenciesAreFound = CurrenciesAreFound(baseCode,targetCode);
        if(!CurrenciesAreFound){
            throw new CurrencyNotFoundException("Не найдена одна из валют");
        }

        List<Integer> ids = getCurrenciesID(baseCode,targetCode);
        Currency baseCurrency = _cdao.getCurrencyByCode(baseCode);
        int baseCurrencyID = ids.get(0);

        Currency targetCurrency = _cdao.getCurrencyByCode(targetCode);
        int targetCurrencyID = ids.get(1);

        int isFoundPair = _dao.findExchangeRatesPair(baseCurrencyID, targetCurrencyID);
        if(isFoundPair==-1){
            throw new ExchangeRateNotFoundException("Курс обмена не найден");
        }

        ExchangeRate rate = _dao.receiveExchangeRatePair(baseCurrency,targetCurrency);

        return rate;
    }

    public ExchangeRate UpdateExchangeRate(String baseCode, String targetCode, double rate) throws SQLException {
        boolean CurrenciesAreFound = CurrenciesAreFound(baseCode,targetCode);
        if(!CurrenciesAreFound){
            throw new CurrencyNotFoundException("Не найдена одна из валют");
        }

        List<Integer> ids = getCurrenciesID(baseCode,targetCode);
        Currency baseCurrency = _cdao.getCurrencyByCode(baseCode);
        int baseCurrencyID = ids.get(0);

        Currency targetCurrency = _cdao.getCurrencyByCode(targetCode);
        int targetCurrencyID = ids.get(1);

        int isFoundPair = _dao.findExchangeRatesPair(baseCurrencyID, targetCurrencyID);
        if(isFoundPair==-1){
            throw new ExchangeRateNotFoundException("Курс обмена не найден");
        }

        ExchangeRate newRate = _dao.updateExchangeRatePair(baseCurrency,targetCurrency, isFoundPair, rate);
        return newRate;
    }

    private boolean CurrenciesAreFound(String baseCode, String targetCode) throws SQLException {
        boolean isFoundBase = _cdao.findCurrency(baseCode);
        if(!isFoundBase){
            throw new CurrencyNotFoundException("Базовая валюта не найдена в бд");
        }

        boolean isFoundTarget = _cdao.findCurrency(targetCode);
        if(!isFoundTarget){
            throw new CurrencyNotFoundException("Целевая валюта не найдена в бд");
        }
        return true;
    }

    private List<Integer> getCurrenciesID(String baseCode, String targetCode){
        Currency baseCurrency = _cdao.getCurrencyByCode(baseCode);
        int baseCurrencyID = baseCurrency.get_id();

        Currency targetCurrency = _cdao.getCurrencyByCode(targetCode);
        int targetCurrencyID = targetCurrency.get_id();
        List<Integer> ids = new ArrayList<>(2);
        ids.add(baseCurrencyID);
        ids.add(targetCurrencyID);
        return Collections.unmodifiableList(ids);
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
