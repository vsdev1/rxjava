package de.idealo.offers.bestprice.optional;

import java.util.Optional;
import java.util.OptionalDouble;

import de.idealo.offers.bestprice.model.Currency;
import de.idealo.offers.bestprice.model.Offer;
import de.idealo.offers.bestprice.model.Shop;
import de.idealo.offers.bestprice.service.DiscountService;
import de.idealo.offers.bestprice.service.ExchangeRateService;
import de.idealo.offers.bestprice.service.OfferService;
import de.idealo.offers.bestprice.service.Util;

public class BestPriceFinderWithOptional implements IBestPriceFinderWithOptional {

    @Override
    public Optional<Double> getOptionalDiscountedPrice(final Shop shop, final String product) {

        return OfferService.getOptionalOffer(shop, product).map(DiscountService::applyDiscount).map(Offer::getPrice);
    }

    @Override
    public Optional<Double> getOptionalAveragePrice(String product, Currency targetCurrency) throws Exception {

        // OptionalDouble vs Optional<Double> ... aaaaaaaargh, why Sun/Oracle, why!???!?

        final OptionalDouble optAvg = Shop.SHOPS.stream()
                .map(shop -> OfferService.getOptionalOffer(shop, product)
                        .map(DiscountService::applyDiscount)
                        .map(offer -> offer.withNewPriceAndCurrency(
                                Util.roundTo2DecimalPlaces(offer.getPrice() * ExchangeRateService.getRate(shop.getCurrency(), targetCurrency)),
                                targetCurrency)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .mapToDouble(Offer::getPrice)
                .average();

        return optAvg.isPresent() ? Optional.of(Util.roundTo2DecimalPlaces(optAvg.getAsDouble())) : Optional.empty(); // :(
    }
}
