package org.coding.reactive.rxjava.bestprice.service;

import static org.coding.reactive.rxjava.bestprice.model.DiscountCode.*;

import java.util.EnumMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.coding.reactive.rxjava.bestprice.model.DiscountCode;
import org.coding.reactive.rxjava.bestprice.model.Offer;
import rx.Observable;

public class DiscountService {

    private static final Logger LOG = LoggerFactory.getLogger(DiscountService.class);

    private static final Map<DiscountCode, Integer> DISCOUNTS = new EnumMap<>(DiscountCode.class);

    private static final long SERVICE_DELAY_MS = 100L;

    static {
        DISCOUNTS.put(NONE, 0);
        DISCOUNTS.put(SILVER, 5);
        DISCOUNTS.put(GOLD, 10);
        DISCOUNTS.put(PLATINUM, 15);
        DISCOUNTS.put(DIAMOND, 20);
    }

    public static Observable<Offer> applyDiscount(Offer offer) {

        LOG.info("Delaying {}ms", SERVICE_DELAY_MS);
        Util.delay(SERVICE_DELAY_MS);
        if (offer == null) {
            return Observable.empty();
        }

        return Observable.just(offer.withDiscountedPrice(
                offer.isDiscountApplied()
                        ? offer.getPrice()
                        : Util.roundTo2DecimalPlaces(offer.getPrice() * (100 - DISCOUNTS.get(offer.getDiscountCode())) / 100)));
    }
}
