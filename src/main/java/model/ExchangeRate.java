package model;

import java.math.BigDecimal;

public class ExchangeRate {
    private final int _id;
    private final Currency _baseCurrency;
    private final Currency _targetCurrency;
    private final BigDecimal _rate;



    public ExchangeRate(int id, Currency baseCurrency, Currency targetCurrency, BigDecimal rate){
        _id = id;
        _baseCurrency = baseCurrency;
        _targetCurrency = targetCurrency;
        _rate = rate;
    }


    public int getId() { return _id; }


    public Currency getBaseCurrencyID() {
        return _baseCurrency;
    }


    public Currency getTargetCurrencyID() {
        return _targetCurrency;
    }


    public BigDecimal getRate() { return _rate; }

}
