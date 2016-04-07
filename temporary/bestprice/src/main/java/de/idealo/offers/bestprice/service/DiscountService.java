package de.idealo.offers.bestprice.service;

import static de.idealo.offers.bestprice.model.DiscountCode.DIAMOND;
import static de.idealo.offers.bestprice.model.DiscountCode.GOLD;
import static de.idealo.offers.bestprice.model.DiscountCode.NONE;
import static de.idealo.offers.bestprice.model.DiscountCode.PLATINUM;
import static de.idealo.offers.bestprice.model.DiscountCode.SILVER;

import java.util.EnumMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.idealo.offers.bestprice.model.DiscountCode;
import de.idealo.offers.bestprice.model.Offer;

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

    public static Offer applyDiscount(Offer offer) {

        LOG.info("Delaying {}ms", SERVICE_DELAY_MS);
        Util.delay(SERVICE_DELAY_MS);
        return offer.withDiscountedPrice(
                offer.isDiscountApplied()
                        ? offer.getPrice()
                        : Util.roundTo2DecimalPlaces(offer.getPrice() * (100 - DISCOUNTS.get(offer.getDiscountCode())) / 100));
    }
}
