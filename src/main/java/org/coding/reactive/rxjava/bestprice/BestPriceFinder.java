package org.coding.reactive.rxjava.bestprice;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.coding.reactive.rxjava.common.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.coding.reactive.rxjava.bestprice.model.Currency;
import org.coding.reactive.rxjava.bestprice.model.Offer;
import org.coding.reactive.rxjava.bestprice.model.Shop;
import org.coding.reactive.rxjava.bestprice.service.DiscountService;
import org.coding.reactive.rxjava.bestprice.service.ExchangeRateService;
import org.coding.reactive.rxjava.bestprice.service.OfferService;
import rx.Observable;

public class BestPriceFinder {

    private static final Logger LOG = LoggerFactory.getLogger(BestPriceFinder.class);

    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    /**
     * @param shop
     *            shop
     * @param product
     *            product name
     * @return discounted price for product offered by shop, in shop's currency
     * @throws Exception
     *             when interrupted waiting for/retrieving async results or on async exceptions
     */
    public Double getDiscountedPriceNullable(final Shop shop, final String product) throws InterruptedException, ExecutionException {
        final Observable<Offer> offerObservable = OfferService.getOfferNullable(shop, product)
                .filter(offer -> offer != null)
                .flatMap(DiscountService::applyDiscount);

        return Optional.ofNullable(offerObservable.toBlocking().single()).map(Offer::getPrice).orElse(null);
          // TODO: TO BE IMPLEMENTED AS CODING DAY EXERCISE
//        return 0.0;
    }

    /**
     * @param shop
     *            shop
     * @param product
     *            product name
     * @return discounted price for product offered by shop or fallback, in shop's currency
     * @throws NotFoundException
     *             if no offer for product in any shop
     * @throws Exception
     *             when interrupted waiting for/retrieving async results or on async exceptions
     */
    public Double getDiscountedPriceWithException(Shop shop, String product) throws InterruptedException, ExecutionException {
        // TODO: TO BE IMPLEMENTED AS CODING DAY EXERCISE
        return 0.0;
    }

    /**
     * @param product
     *            product name
     * @param targetCurrency
     *            target currency
     * @return list of offers with discounted prices in target currency
     * @throws Exception
     *             when interrupted waiting for/retrieving async results or on async exceptions
     */
    public List<Offer> findOffers(String product, Currency targetCurrency) throws InterruptedException, ExecutionException {
        final Observable<Offer> offersObservable = Observable.from(Shop.SHOPS)
                .flatMap(shop -> OfferService.getOfferNullable(shop, product))
                .filter(offer -> offer != null)
                .flatMap(offer -> DiscountService.applyDiscount(offer));

        Utils.subscribePrint(offersObservable, "Offers Observable");

        final Observable<Double> exchangeRateObservable = Observable.from(Shop.SHOPS)
                .flatMap(shop -> ExchangeRateService.getRate(shop.getCurrency(), targetCurrency));

        Utils.subscribePrint(exchangeRateObservable, "ExchangeRate Observable");

//        offersObservable.flatMap(offer -> exchangeRateObservable)
//
//        offersObservable.ambWith()

        // TODO: TO BE IMPLEMENTED AS CODING DAY EXERCISE
        return null;
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

//    private CompletableFuture<Offer> getOfferNullableAsync(Shop shop, String product) {
//        return CompletableFuture.supplyAsync(() -> OfferService.getOfferNullable(shop, product), executorService);
//    }
//
//    private CompletableFuture<Offer> getOfferWithExceptionAsync(Shop shop, String product) {
//        return CompletableFuture.supplyAsync(() -> OfferService.getOfferWithException(shop, product), executorService);
//    }
//
//    private CompletableFuture<Offer> applyDiscountAsync(Offer offer) {
//        return CompletableFuture.supplyAsync(() -> DiscountService.applyDiscount(offer), executorService);
//    }
//
//    private CompletableFuture<Double> getRateAsync(Currency from, Currency to) {
//        return CompletableFuture.supplyAsync(() -> ExchangeRateService.getRate(from, to), executorService);
//    }
}
