package service;

import DAO.CurrencyDAO;
import DAO.ExchangeRatesDAO;
import exceptions.CurrencyNotFoundException;
import exceptions.ExchangeRateAlreadyExistsException;
import exceptions.ExchangeRateNotFoundException;
import model.Currency;
import model.ExchangeCurrency;
import model.ExchangeRate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ExchangeRatesService {
    private final ExchangeRatesDAO _dao = new ExchangeRatesDAO();
    private final CurrencyDAO _cdao = new CurrencyDAO();

    public List<ExchangeRate> getAllExchangeRates() throws SQLException {
        return _dao.getAllExchangeRates();
    }

    public ExchangeRate addNewExchangeRate(String baseCurrencyCode,
                                           String targetCurrencyCode, BigDecimal rate) throws SQLException {
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

    public ExchangeRate UpdateExchangeRate(String baseCode, String targetCode, BigDecimal rate) throws SQLException {
        Currency[] currencies = getCurrenciesPair(baseCode, targetCode);
        Currency baseCurrency = currencies[0];
        Currency targetCurrency = currencies[1];
        int baseCurrencyID = baseCurrency.get_id();
        int targetCurrencyID = targetCurrency.get_id();
        int pairID = ensurePairExists(baseCurrencyID, targetCurrencyID);

        ExchangeRate newRate = _dao.updateExchangeRatePair(baseCurrency, targetCurrency, pairID, rate);
        return newRate;
    }

    public ExchangeCurrency ExchangeCurrency(String baseCode, String targetCode, BigDecimal amount) throws SQLException {
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
    private Currency[] getCurrenciesPair(String baseCode, String targetCode) throws SQLException {
        Optional<Currency> baseCurrency = _cdao.getCurrencyByCode(baseCode);
        if (!baseCurrency.isPresent()) {
            throw new CurrencyNotFoundException("Базовая валюта не найдена в бд");
        }

        Optional<Currency> targetCurrency = _cdao.getCurrencyByCode(targetCode);
        if (!targetCurrency.isPresent()) {
            throw new CurrencyNotFoundException("Целевая валюта не найдена в бд");
        }

        return new Currency[]{baseCurrency.get(), targetCurrency.get()};
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

    private ExchangeCurrency tryExchangeThroughUSD(Currency baseCurrency, Currency targetCurrency, BigDecimal amount)
            throws SQLException {
        Optional<Currency> usdCurrency = _cdao.getCurrencyByCode("USD");
        if (!usdCurrency.isPresent()) {
            throw new CurrencyNotFoundException("Валюта USD не найдена в бд");
        }
        Currency USD = usdCurrency.get();
        int USDBase = _dao.findExchangeRatesPair(USD.get_id(), baseCurrency.get_id());
        int USDTarget = _dao.findExchangeRatesPair(USD.get_id(), targetCurrency.get_id());
        if (USDBase != -1 && USDTarget != -1) {
            ExchangeCurrency USDBASE = exchange(baseCurrency, USD, amount, true);
            ExchangeCurrency USDTARGET = exchange(USD, targetCurrency, BigDecimal.ONE, false);
            ExchangeCurrency exchange = new ExchangeCurrency(baseCurrency, targetCurrency,
                    USDTARGET.getRate(),
                    amount.divide(USDBASE.getRate(), 12, RoundingMode.HALF_UP),
                    USDBASE.getConvertedAmount().multiply(USDTARGET.getConvertedAmount()));
            return exchange;
        }

        return null;
    }

    private ExchangeCurrency exchange(Currency baseCurrency,Currency targetCurrency,
                                      BigDecimal amount, boolean isReversed)
            throws SQLException {
        ExchangeRate rate = _dao.receiveExchangeRatePair(baseCurrency, targetCurrency);
        BigDecimal convertedAmount;
        if(!isReversed)
            convertedAmount = rate.getRate().multiply(amount);

        else
            convertedAmount = amount.divide(rate.getRate(), 12, RoundingMode.HALF_UP);

        ExchangeCurrency exchange = new ExchangeCurrency(baseCurrency,targetCurrency, rate.getRate(),amount,
                convertedAmount);
        return exchange;
    }

}
