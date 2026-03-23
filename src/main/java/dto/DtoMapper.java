package dto;

import model.Currency;
import model.ExchangeCurrency;
import model.ExchangeRate;

public final class DtoMapper {
    private DtoMapper() {
    }

    public static CurrencyResponseDto toCurrencyDto(Currency currency) {
        return new CurrencyResponseDto(
                currency.get_id(),
                currency.get_code(),
                currency.get_fullName(),
                currency.get_sign()
        );
    }

    public static ExchangeRateResponseDto toExchangeRateDto(ExchangeRate rate) {
        return new ExchangeRateResponseDto(
                rate.getId(),
                toCurrencyDto(rate.getBaseCurrencyID()),
                toCurrencyDto(rate.getTargetCurrencyID()),
                rate.getRate()
        );
    }

    public static ExchangeCurrencyResponseDto toExchangeCurrencyDto(ExchangeCurrency exchange) {
        return new ExchangeCurrencyResponseDto(
                toCurrencyDto(exchange.getBaseCurrencyID()),
                toCurrencyDto(exchange.getTargetCurrencyID()),
                exchange.getRate(),
                exchange.getAmount(),
                exchange.getConvertedAmount()
        );
    }
}
