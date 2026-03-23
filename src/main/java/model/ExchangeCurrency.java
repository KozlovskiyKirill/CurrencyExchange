package model;

public class ExchangeCurrency {
    private final Currency _baseCurrency;
    private final Currency _targetCurrency;
    private final double _rate;
    private double _amount;
    private double _convertedAmount;

    public ExchangeCurrency(Currency baseCurrency, Currency targetCurrency, double rate, double amount,
                            double convertedAmount) {
        _baseCurrency = baseCurrency;
        _targetCurrency = targetCurrency;
        _rate = rate;
        _amount = amount;
        _convertedAmount = convertedAmount;
    }

    public Currency getBaseCurrencyID() {
        return _baseCurrency;
    }


    public Currency getTargetCurrencyID() {
        return _targetCurrency;
    }


    public double getRate() { return _rate; }

    public double getAmount() {
        return _amount;
    }

    public double getConvertedAmount() {
        return _convertedAmount;
    }
}
