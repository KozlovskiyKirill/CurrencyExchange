package service;

import DAO.CurrencyDAO;
import model.Currency;

import java.sql.SQLException;
import java.util.List;

public class CurrencyService {
    CurrencyDAO _currencyDao = new CurrencyDAO();


    public List<Currency> getAllCurrencies() throws SQLException {
        System.out.print("Зашли в сервис");
        List currency = _currencyDao.getAll();
        return currency;
    }
}
