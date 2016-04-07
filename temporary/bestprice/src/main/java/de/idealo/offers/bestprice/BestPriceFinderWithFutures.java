package de.idealo.offers.bestprice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import de.idealo.offers.bestprice.model.Currency;
import de.idealo.offers.bestprice.model.Offer;
import de.idealo.offers.bestprice.model.Shop;
import de.idealo.offers.bestprice.service.DiscountService;
import de.idealo.offers.bestprice.service.ExchangeRateService;
import de.idealo.offers.bestprice.service.NotFoundException;
import de.idealo.offers.bestprice.service.OfferService;
import de.idealo.offers.bestprice.service.Util;

public class BestPriceFinderWithFutures implements IBestPriceFinder {

    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Override
    public Double getDiscountedPriceNullable(final Shop shop, final String product) throws ExecutionException, InterruptedException {
        Future<Offer> offerFuture = executorService.submit(new Callable<Offer>() {
            @Override
            public Offer call() {
                return OfferService.getOfferNullable(shop, product);
            }
        });
        Offer offer = offerFuture.get();
        if (offer != null) {
            return DiscountService.applyDiscount(offer).getPrice(); // CDAN: why is this one synchronous??
        }
        return null;
    }

    @Override
    public Double getDiscountedPriceWithException(Shop shop, String product) throws InterruptedException, ExecutionException {
        Future<Offer> offerFuture = executorService.submit(new Callable<Offer>() {
            @Override
            public Offer call() {
                return OfferService.getOfferWithException(shop, product);
            }
        });
        Offer offer;
        try {
            offer = offerFuture.get();
        } catch (ExecutionException e) {
            if (e.getCause() instanceof NotFoundException) {
                throw (NotFoundException) e.getCause();
            }
            throw e;
        }
        return DiscountService.applyDiscount(offer).getPrice();
    }

    @Override
    public List<Offer> findOffers(String product, Currency targetCurrency) throws ExecutionException, InterruptedException {
        List<Offer> result = new ArrayList<>();
        for (Shop shop : Shop.SHOPS) {

            final Future<Offer> offerFuture = executorService.submit(new Callable<Offer>() {
                @Override
                public Offer call() {
                    return OfferService.getOfferNullable(shop, product);
                }
            });
            Future<Offer> discountedOfferFuture = executorService.submit(new Callable<Offer>() {
                @Override
                public Offer call() throws Exception {
                    if (offerFuture.get() != null) {
                        return DiscountService.applyDiscount(offerFuture.get());
                    } else {
                        return null;
                    }
                }
            });
            Future<Double> rateFuture = executorService.submit(new Callable<Double>() {
                @Override
                public Double call() {
                    if (shop.getCurrency() != null) {
                        return ExchangeRateService.getRate(shop.getCurrency(), targetCurrency);
                    } else {
                        return null;
                    }
                }
            });
            Offer discountedOffer = discountedOfferFuture.get();
            if (discountedOffer != null && rateFuture.get() != null) {
                Double price = Util.roundTo2DecimalPlaces(discountedOffer.getPrice() * rateFuture.get());
                result.add(discountedOffer.withNewPriceAndCurrency(price, targetCurrency));
            }
        }
        return result;
    }

    @Override
    public Double getAveragePriceNullable(String product, Currency targetCurrency) throws ExecutionException, InterruptedException {
        Double sumOfAllPrices = 0.0;
        int shopsWithPrices = 0;

        Map<String, Future<Double>> priceFutures = new HashMap<>(); // shop->price
        Map<String, Future<Double>> rateFutures = new HashMap<>(); // shop->rate

        for (Shop shop : Shop.SHOPS) {

            final Future<Offer> offerFuture = executorService.submit(new Callable<Offer>() {
                @Override
                public Offer call() {
                    return OfferService.getOfferNullable(shop, product);
                }
            });
            Future<Double> priceFuture = executorService.submit(new Callable<Double>() {
                @Override
                public Double call() throws Exception {
                    if (offerFuture.get() != null) {
                        return DiscountService.applyDiscount(offerFuture.get()).getPrice();
                    } else {
                        return null;
                    }
                }
            });
            priceFutures.put(shop.getName(), priceFuture);
            Future<Double> rateFuture = executorService.submit(new Callable<Double>() {
                @Override
                public Double call() {
                    if (shop.getCurrency() != null) {
                        return ExchangeRateService.getRate(shop.getCurrency(), targetCurrency);
                    } else {
                        return null;
                    }
                }
            });
            rateFutures.put(shop.getName(), rateFuture);

            // see optimization using the maps below
            //            if (priceFuture.get() != null && rateFuture.get() != null) {
            //                sumOfAllPrices += Util.roundTo2DecimalPlaces(priceFuture.get() * rateFuture.get());
            //                ++shopsWithPrices;
            //            }
        }

        // optimization 
        Map<String, Double> prices = getMap(priceFutures);
        Map<String, Double> rates = getMap(rateFutures);

        for (Shop shop : Shop.SHOPS) {
            Double price = prices.get(shop.getName());
            Double rate = rates.get(shop.getName());
            if (price != null && rate != null) {
                sumOfAllPrices += Util.roundTo2DecimalPlaces(price * rate);
                ++shopsWithPrices;
            }
        }

        if (shopsWithPrices > 0) {
            return Util.roundTo2DecimalPlaces(sumOfAllPrices / shopsWithPrices);
        } else {
            return null;
        }
    }

    private <T> Map<String, T> getMap(Map<String, Future<T>> futureMap) throws ExecutionException, InterruptedException {
        Map<String, T> result = new HashMap<>();
        for (Map.Entry<String, Future<T>> futureEntry : futureMap.entrySet()) {
            T val = futureEntry.getValue().get();
            if (val != null) {
                result.put(futureEntry.getKey(), val);
            }
        }
        return result;
    }
}
