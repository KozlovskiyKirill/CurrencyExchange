package service;

import DAO.CurrencyDAO;
import DAO.ExchangeRatesDAO;
import exceptions.CurrencyNotFoundException;
import exceptions.ExchangeRateAlreadyExistsException;
import exceptions.ExchangeRateNotFoundException;
import model.Currency;
import model.ExchangeCurrency;
import model.ExchangeRate;

import java.sql.SQLException;
import java.util.List;

public class ExchangeRatesService {
    private ExchangeRatesDAO _dao = new ExchangeRatesDAO();
    private CurrencyDAO _cdao = new CurrencyDAO();

    public List<ExchangeRate> getAllExchangeRates() {
        List<ExchangeRate> rates = _dao.getAllExchangeRates();
        return rates;
    }

    public ExchangeRate addNewExchangeRate(String baseCurrencyCode,
                                           String targetCurrencyCode, double rate) throws SQLException {
        Currency[] currencies = getCurrenciesPair(baseCurrencyCode, targetCurrencyCode);
        Currency baseCurrency = currencies[0];
        Currency targetCurrency = currencies[1];
        int baseCurrencyID = baseCurrency.get_id();
        int targetCurrencyID = targetCurrency.get_id();
        ensurePairDoesNotExist(baseCurrencyID, targetCurrencyID);

        int newRateID = _dao.addNewExchangeRate(baseCurrencyID, targetCurrencyID, rate);
        return new ExchangeRate(newRateID, baseCurrency, targetCurrency, rate);
    }

    public ExchangeRate findExchangeRatePairByCode(String baseCode, String targetCode) throws SQLException {
        Currency[] currencies = getCurrenciesPair(baseCode, targetCode);
        Currency baseCurrency = currencies[0];
        Currency targetCurrency = currencies[1];
        int baseCurrencyID = baseCurrency.get_id();
        int targetCurrencyID = targetCurrency.get_id();
        ensurePairExists(baseCurrencyID, targetCurrencyID);

        ExchangeRate rate = _dao.receiveExchangeRatePair(baseCurrency, targetCurrency);
        return rate;
    }

    public ExchangeRate UpdateExchangeRate(String baseCode, String targetCode, double rate) throws SQLException {
        Currency[] currencies = getCurrenciesPair(baseCode, targetCode);
        Currency baseCurrency = currencies[0];
        Currency targetCurrency = currencies[1];
        int baseCurrencyID = baseCurrency.get_id();
        int targetCurrencyID = targetCurrency.get_id();
        int pairID = ensurePairExists(baseCurrencyID, targetCurrencyID);

        ExchangeRate newRate = _dao.updateExchangeRatePair(baseCurrency, targetCurrency, pairID, rate);
        return newRate;
    }

    public ExchangeCurrency ExchangeCurrency(String baseCode, String targetCode, double amount) throws SQLException {
        Currency[] currencies = getCurrenciesPair(baseCode, targetCode);
        Currency baseCurrency = currencies[0];
        Currency targetCurrency = currencies[1];
        int baseCurrencyID = baseCurrency.get_id();
        int targetCurrencyID = targetCurrency.get_id();

        int isFoundPair = _dao.findExchangeRatesPair(baseCurrencyID, targetCurrencyID);
        if (isFoundPair != -1) {
            ExchangeCurrency exchange = exchange(baseCurrency, targetCurrency, amount, false);
            return exchange;
        }

        int isFoundReversedPair = _dao.findExchangeRatesPair(targetCurrencyID, baseCurrencyID);
        if (isFoundReversedPair != -1) {
            ExchangeCurrency exchange = exchange(targetCurrency, baseCurrency, amount, true);
            return exchange;
        }

        ExchangeCurrency exchange = tryExchangeThroughUSD(baseCurrency, targetCurrency, amount);
        return exchange;
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

    private Currency[] getCurrenciesPair(String baseCode, String targetCode) throws SQLException {
        boolean CurrenciesAreFound = CurrenciesAreFound(baseCode, targetCode);
        if (!CurrenciesAreFound) {
            throw new CurrencyNotFoundException("Не найдена одна из валют");
        }

        Currency baseCurrency = _cdao.getCurrencyByCode(baseCode);
        Currency targetCurrency = _cdao.getCurrencyByCode(targetCode);
        return new Currency[]{baseCurrency, targetCurrency};
    }

    private int ensurePairExists(int baseCurrencyID, int targetCurrencyID) throws SQLException {
        int pairID = _dao.findExchangeRatesPair(baseCurrencyID, targetCurrencyID);
        if (pairID == -1) {
            throw new ExchangeRateNotFoundException("Курс обмена не найден");
        }
        return pairID;
    }

    private void ensurePairDoesNotExist(int baseCurrencyID, int targetCurrencyID) throws SQLException {
        int pairID = _dao.findExchangeRatesPair(baseCurrencyID, targetCurrencyID);
        if (pairID != -1) {
            throw new ExchangeRateAlreadyExistsException("Курс обмена уже существует");
        }
    }

    private ExchangeCurrency tryExchangeThroughUSD(Currency baseCurrency, Currency targetCurrency, double amount)
            throws SQLException {
        Currency USD = _cdao.getCurrencyByCode("USD");
        int USDBase = _dao.findExchangeRatesPair(USD.get_id(), baseCurrency.get_id());
        int USDTarget = _dao.findExchangeRatesPair(USD.get_id(), targetCurrency.get_id());
        if (USDBase != -1 && USDTarget != -1) {
            ExchangeCurrency USDBASE = exchange(baseCurrency, USD, amount, true);
            ExchangeCurrency USDTARGET = exchange(USD, targetCurrency, 1, false);
            ExchangeCurrency exchange = new ExchangeCurrency(baseCurrency, targetCurrency,
                    USDTARGET.getRate(), amount / USDBASE.getRate(),
                    USDBASE.getConvertedAmount() * USDTARGET.getConvertedAmount());
            return exchange;
        }

        return null;
    }

    private ExchangeCurrency exchange(Currency baseCurrency,Currency targetCurrency,
                                      double amount, boolean isReversed)
            throws SQLException {
        ExchangeRate rate = _dao.receiveExchangeRatePair(baseCurrency, targetCurrency);
        double convertedAmount = 0;
        if(!isReversed)
            convertedAmount = rate.getRate()*amount;

        else
            convertedAmount = 1/ rate.getRate()*amount;

        ExchangeCurrency exchange = new ExchangeCurrency(baseCurrency,targetCurrency, rate.getRate(),amount,
                convertedAmount);
        return exchange;
    }

}
