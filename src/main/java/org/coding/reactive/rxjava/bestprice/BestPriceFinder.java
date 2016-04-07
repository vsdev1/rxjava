package org.coding.reactive.rxjava.bestprice;

import org.coding.reactive.rxjava.bestprice.model.Currency;
import org.coding.reactive.rxjava.bestprice.model.Offer;
import org.coding.reactive.rxjava.bestprice.model.Shop;
import org.coding.reactive.rxjava.bestprice.service.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

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
//          // TODO: TO BE IMPLEMENTED AS CODING DAY EXERCISE
        return Observable.empty();
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
//        // TODO: TO BE IMPLEMENTED AS CODING DAY EXERCISE
        return Observable.empty();
    }

    /**
     * @param product
     *            product name
     * @param targetCurrency
     *            target currency
     * @return observable of offers with discounted prices in target currency
     *          or an empty observable if no offer was found for the product
     */
    public Observable<Offer> findOffers(String product, Currency targetCurrency) {
        // TODO: TO BE IMPLEMENTED AS CODING DAY EXERCISE
        return Observable.empty();
    }

    /**
     * @param product
     *            product name
     * @param targetCurrency
     *            target currency
     * @return average discounted price for the product in target currency
     *          or an empty observable if no offer was found for the product
     */
    public Observable<Double> getAveragePriceNullable(String product, Currency targetCurrency) {
//        // TODO: TO BE IMPLEMENTED AS CODING DAY EXERCISE
        return Observable.empty();
    }
}
