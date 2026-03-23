package model;

import java.math.BigDecimal;

public class ExchangeCurrency {
    private final Currency _baseCurrency;
    private final Currency _targetCurrency;
    private final BigDecimal _rate;
    private final BigDecimal _amount;
    private final BigDecimal _convertedAmount;

    public ExchangeCurrency(Currency baseCurrency, Currency targetCurrency, BigDecimal rate, BigDecimal amount,
                            BigDecimal convertedAmount) {
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


    public BigDecimal getRate() { return _rate; }

    public BigDecimal getAmount() {
        return _amount;
    }

    public BigDecimal getConvertedAmount() {
        return _convertedAmount;
    }
}
