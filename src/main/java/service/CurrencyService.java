package service;

import DAO.CurrencyDAO;
import model.Currency;

import java.sql.SQLException;
import java.util.List;

public class CurrencyService {
    CurrencyDAO _currencyDao = new CurrencyDAO();


    public List<Currency> getAllCurrencies() throws SQLException {
        System.out.print("Зашли в сервис");
        List<Currency> currency = _currencyDao.getAllCurrencies();
        return currency;
    }

    public Currency addCurrency(String name, String code, String sign) throws SQLException {
        boolean isFound = _currencyDao.findCurrency(code);
        if(!isFound){
            Currency currency = _currencyDao.addNewCurrency(name,code,sign);
            return currency;
        }
        else throw new CurrencyAlreadyExistsException("Такая валюта уже существует");
    }

    public static class CurrencyAlreadyExistsException extends RuntimeException {
        public CurrencyAlreadyExistsException(String message) {
            super(message);
        }
    }
}
