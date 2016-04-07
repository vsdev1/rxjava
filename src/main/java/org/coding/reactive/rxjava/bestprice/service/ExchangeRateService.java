package org.coding.reactive.rxjava.bestprice.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.coding.reactive.rxjava.bestprice.model.Currency;
import rx.Observable;

public class ExchangeRateService {

    private static final Logger LOG = LoggerFactory.getLogger(ExchangeRateService.class);

    private static final Map<Currency, Double> EXCHANGE_RATES;

    private static final long SERVICE_DELAY_MS = 100L;

    static {
        EXCHANGE_RATES = new HashMap<>();
        EXCHANGE_RATES.put(Currency.USD, 1.0);
        EXCHANGE_RATES.put(Currency.EUR, 1.35387);
        EXCHANGE_RATES.put(Currency.GBP, 1.69715);
        EXCHANGE_RATES.put(Currency.CAD, .92106);
        EXCHANGE_RATES.put(Currency.MXN, .07683);
    }

    public static Observable<Double> getRate(Currency from, Currency to) {
        LOG.info("Delaying {}ms", SERVICE_DELAY_MS);
        Util.delay(SERVICE_DELAY_MS);
        return Observable.just(EXCHANGE_RATES.get(from) / EXCHANGE_RATES.get(to));
    }
}
