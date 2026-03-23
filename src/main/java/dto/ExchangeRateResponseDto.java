package dto;

import java.math.BigDecimal;

public class ExchangeRateResponseDto {
    private final int id;
    private final CurrencyResponseDto baseCurrency;
    private final CurrencyResponseDto targetCurrency;
    private final BigDecimal rate;

    public ExchangeRateResponseDto(int id, CurrencyResponseDto baseCurrency, CurrencyResponseDto targetCurrency,
                                   BigDecimal rate) {
        this.id = id;
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
    }

    public int getId() {
        return id;
    }

    public CurrencyResponseDto getBaseCurrency() {
        return baseCurrency;
    }

    public CurrencyResponseDto getTargetCurrency() {
        return targetCurrency;
    }

    public BigDecimal getRate() {
        return rate;
    }
}
