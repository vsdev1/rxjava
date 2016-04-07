package org.coding.reactive.rxjava.bestprice;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    public Double getDiscountedPriceNullable(final Shop shop, final String product) throws InterruptedException, ExecutionException {
        final Observable<Offer> offerObservable = Observable.just(OfferService.getOfferNullable(shop, product))
                .filter(offer -> offer != null)
                .map(DiscountService::applyDiscount);

        return offerObservable.toBlocking().first().getPrice();
//
//
//        final CompletableFuture<Offer> offerCompletableFuture = getOfferNullableAsync(shop, product)
//                .thenApply(offer -> Optional.ofNullable(offer).map(DiscountService::applyDiscount).orElse(null));
//
//        return Optional.ofNullable(offerCompletableFuture.get()).map(Offer::getPrice).orElse(null);
//
//        shop.getDiscount()
//        // TO BE IMPLEMENTED AS CODING DAY EXERCISE
//        // using getOfferNullableAsync(shop, product) and DiscountService::applyDiscount
//        return 0.0;
    }

    public Double getDiscountedPriceWithException(Shop shop, String product) throws InterruptedException, ExecutionException {
        // TO BE IMPLEMENTED AS CODING DAY EXERCISE
        // using getOfferWithExceptionAsync() and DiscountService::applyDiscount
        return 0.0;
    }

    public List<Offer> findOffers(String product, Currency targetCurrency) throws InterruptedException, ExecutionException {
        // TO BE IMPLEMENTED AS CODING DAY EXERCISE
        // using getOfferNullableAsync(shop, product) and applyDiscountAsync() and getRateAsync()
        return null;
    }

    public Double getAveragePriceNullable(String product, Currency targetCurrency) throws Exception {
        // TO BE IMPLEMENTED AS CODING DAY EXERCISE
        // using getOfferNullableAsync(shop, product) and applyDiscountAsync() and getRateAsync()
        return 0.0;
    }

    private CompletableFuture<Offer> getOfferNullableAsync(Shop shop, String product) {
        return CompletableFuture.supplyAsync(() -> OfferService.getOfferNullable(shop, product), executorService);
    }

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
