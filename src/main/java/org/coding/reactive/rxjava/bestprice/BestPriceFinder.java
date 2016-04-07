package org.coding.reactive.rxjava.bestprice;

import org.coding.reactive.rxjava.bestprice.model.Currency;
import org.coding.reactive.rxjava.bestprice.model.Offer;
import org.coding.reactive.rxjava.bestprice.model.Shop;
import org.coding.reactive.rxjava.bestprice.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BestPriceFinder {

    private static final Logger LOG = LoggerFactory.getLogger(BestPriceFinder.class);

    /**
     * @param shop
     *            shop
     * @param product
     *            product name
     * @return discounted price for product offered by shop, in shop's currency
     *          or an empty observable if no offer was found for the product in the given shop
     */
    public Observable<Double> getDiscountedPriceNullable(final Shop shop, final String product) {
         return OfferService.getOfferNullable(shop, product)
               .filter(offer -> offer != null)
               .flatMap(DiscountService::applyDiscount)
               .map(Offer::getPrice);
//          // TODO: TO BE IMPLEMENTED AS CODING DAY EXERCISE
//        return Observable.empty();
    }

    /**
     * @param shop
     *            shop
     * @param product
     *            product name
     * @return discounted price for product offered by shop or fallback, in shop's currency
     * @throws NotFoundException
     *             if no offer for product in the shop
     */
    public Observable<Double> getDiscountedPriceWithException(Shop shop, String product) {
        return OfferService.getOfferWithException(shop, product)
                .flatMap(DiscountService::applyDiscount)
                .map(Offer::getPrice);
//        // TODO: TO BE IMPLEMENTED AS CODING DAY EXERCISE
//        return Observable.empty();
    }

    /**
     * @param product
     *            product name
     * @param targetCurrency
     *            target currency
     * @return list of offers with discounted prices in target currency
     */
    public Observable<Offer> findOffers(String product, Currency targetCurrency) {
        return Observable.from(Shop.SHOPS)
                .flatMap(shop -> getOfferWithExchangeRatePrice(product, shop, targetCurrency));
        // TODO: TO BE IMPLEMENTED AS CODING DAY EXERCISE
//        return Observable.empty();
    }

    private Observable<Offer> getOfferWithExchangeRatePrice(String product, Shop shop, Currency targetCurrency) {
        Observable<Offer> offerObservable = OfferService.getOfferNullable(shop, product)
                .filter(offer -> offer != null)
                .defaultIfEmpty(null)
                .flatMap(DiscountService::applyDiscount);

        Observable<Double> exchangeRateObservable = ExchangeRateService.getRate(shop.getCurrency(), targetCurrency);

        return Observable.combineLatest(offerObservable, exchangeRateObservable,
                (offer, exchangeRate) ->
                        offer.withNewPriceAndCurrency(Util.roundTo2DecimalPlaces(offer.getPrice() * exchangeRate), targetCurrency));
    }

    public static void main(String[] args) throws Exception {
        new BestPriceFinder().findOffers("Samsung S6", Currency.EUR);
    }

    /**
     * @param product
     *            product name
     * @param targetCurrency
     *            target currency
     * @return average discounted price for the product in USD.
     * @throws Exception
     *             when interrupted waiting for/retrieving async results or on async exceptions
     */
    public Double getAveragePriceNullable(String product, Currency targetCurrency) throws Exception {
        // TODO: TO BE IMPLEMENTED AS CODING DAY EXERCISE
        return 0.0;
    }
}
