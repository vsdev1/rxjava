package de.idealo.offers.bestprice;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;

import de.idealo.offers.bestprice.model.Currency;
import de.idealo.offers.bestprice.model.Offer;
import de.idealo.offers.bestprice.model.Shop;
import de.idealo.offers.bestprice.service.DiscountService;
import de.idealo.offers.bestprice.service.ExchangeRateService;
import de.idealo.offers.bestprice.service.OfferService;
import de.idealo.offers.bestprice.service.Util;

public class BestPriceFinderSequential implements IBestPriceFinder {

    @Override
    public Double getDiscountedPriceNullable(final Shop shop, final String product) {

        return Optional.ofNullable(OfferService.getOfferNullable(shop, product)).map(DiscountService::applyDiscount).map(Offer::getPrice).orElse(null);
    }

    @Override
    public Double getDiscountedPriceWithException(final Shop shop, final String product) {
        return DiscountService.applyDiscount(OfferService.getOfferWithException(shop, product)).getPrice();
    }

    @Override
    public List<Offer> findOffers(final String product, Currency targetCurrency) {
        return Shop.SHOPS.stream()
                .map(shop -> OfferService.getOptionalOffer(shop, product)
                        .map(DiscountService::applyDiscount)
                        .map(offer -> offer.withNewPriceAndCurrency(
                                Util.roundTo2DecimalPlaces(offer.getPrice() * ExchangeRateService.getRate(shop.getCurrency(), targetCurrency)),
                                targetCurrency)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());
    }

    @Override
    public Double getAveragePriceNullable(final String product, Currency targetCurrency) {

        final OptionalDouble optAvg = Shop.SHOPS.stream()
                .map(shop -> OfferService.getOfferNullable(shop, product))
                .filter(Objects::nonNull)
                .map(DiscountService::applyDiscount)
                .map(offer -> offer.withNewPriceAndCurrency(
                        Util.roundTo2DecimalPlaces(offer.getPrice() * ExchangeRateService.getRate(offer.getCurrency(), targetCurrency)),
                        targetCurrency))
                .mapToDouble(Offer::getPrice)
                .average();

        return optAvg.isPresent() ? Util.roundTo2DecimalPlaces(optAvg.getAsDouble()) : null; // :(
    }
}
