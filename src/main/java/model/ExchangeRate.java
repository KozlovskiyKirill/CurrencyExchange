package model;

public class ExchangeRate {
    private final int _id;
    private final Currency _baseCurrency;
    private final Currency _targetCurrency;
    private final double _rate;



    public ExchangeRate(int id, Currency baseCurrency, Currency targetCurrency, double rate){
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


    public double getRate() { return _rate; }

}
