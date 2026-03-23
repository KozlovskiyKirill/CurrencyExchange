package service;

import DAO.CurrencyDAO;
import exceptions.CurrencyAlreadyExistsException;
import exceptions.CurrencyNotFoundException;
import model.Currency;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CurrencyService {
    private final CurrencyDAO _currencyDao = new CurrencyDAO();


    public List<Currency> getAllCurrencies() throws SQLException {
        return _currencyDao.getAllCurrencies();
    }

    public Currency addCurrency(String name, String code, String sign) throws SQLException {
        boolean isFound = _currencyDao.findCurrency(code);
        if(!isFound){
            Currency currency = _currencyDao.addNewCurrency(name,code,sign);
            return currency;
        }
        else throw new CurrencyAlreadyExistsException("Такая валюта уже существует");
    }

    public Currency findCurrency(String code) throws SQLException {
        Optional<Currency> currency = _currencyDao.getCurrencyByCode(code);
        if(currency.isPresent()){
            return currency.get();
        }
        throw new CurrencyNotFoundException("Валюта не найдена");
    }
}
