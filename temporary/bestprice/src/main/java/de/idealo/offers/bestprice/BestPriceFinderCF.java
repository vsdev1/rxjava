package de.idealo.offers.bestprice;

import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.idealo.offers.bestprice.model.Currency;
import de.idealo.offers.bestprice.model.Offer;
import de.idealo.offers.bestprice.model.Shop;
import de.idealo.offers.bestprice.service.DiscountService;
import de.idealo.offers.bestprice.service.ExchangeRateService;
import de.idealo.offers.bestprice.service.NotFoundException;
import de.idealo.offers.bestprice.service.OfferService;
import de.idealo.offers.bestprice.service.Util;

public class BestPriceFinderCF implements IBestPriceFinder {

    private static final Logger LOG = LoggerFactory.getLogger(BestPriceFinderCF.class);

    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Override
    public Double getDiscountedPriceNullable(final Shop shop, final String product) throws InterruptedException, ExecutionException {
        final CompletableFuture<Offer> offerCompletableFuture = getOfferNullableAsync(shop, product)
                .thenApply(offer -> Optional.ofNullable(offer).map(DiscountService::applyDiscount).orElse(null));

        return Optional.ofNullable(offerCompletableFuture.get()).map(Offer::getPrice).orElse(null);
    }

    @Override
    public Double getDiscountedPriceWithException(Shop shop, String product) throws InterruptedException, ExecutionException {

        final CompletableFuture<Offer> offerCompletableFuture = getOfferWithExceptionAsync(shop, product)
                .whenComplete((offer, t) -> {
                    LOG.warn("async exception: {}", t);
                    if (t instanceof CompletionException && t.getCause() instanceof NotFoundException) {
                        // rethrowing the cause is actually nonsense (could just omit the whole handler)
                        // just for demonstration of .handle and .exceptionally APIs
                        throw (NotFoundException) t.getCause();
                    }
                    // .whenComplete cannot return anything, the rest of the chain is executed on offer or t
                    // if t is not null, offer is null and will cause an NPE in applyDiscount
                })
                // same as .exceptionally(t -> { ...; return null; }) // exceptionally cannot return another type
                // same as .handle((offer, t) -> { if (t != null) { ...; } return offer; }) // handle can return another type
                .thenApply(DiscountService::applyDiscount);

        try {
            return offerCompletableFuture.get().getPrice();
        } catch (ExecutionException e) {
            if (e.getCause() instanceof NotFoundException) {
                LOG.info("extracted async NotFoundException - re-thrown because offer not found");
                throw (NotFoundException) e.getCause();
            } else if (e.getCause() instanceof NullPointerException) {
                LOG.error("extracted async NullPointerException - something else happened", e.getCause());
            }
            throw e;
        }
    }

    @Override
    public List<Offer> findOffers(String product, Currency targetCurrency) throws InterruptedException, ExecutionException {
        final List<CompletableFuture<Offer>> cfs = Shop.SHOPS.stream()
                .map(shop -> getOfferNullableAsync(shop, product)
                        .thenCompose(this::applyDiscountAsync) // NPE?!
                        .thenCombine(
                                getRateAsync(shop.getCurrency(), targetCurrency),
                                (discountedOffer, rate) -> discountedOffer.withNewPriceAndCurrency(
                                        Util.roundTo2DecimalPlaces(discountedOffer.getPrice() * rate),
                                        targetCurrency)))
                .collect(Collectors.toList());

        return Util.sequence(cfs).get();
    }

    @Override
    public Double getAveragePriceNullable(String product, Currency targetCurrency) throws Exception {
        final List<CompletableFuture<Double>> prices = Shop.SHOPS.stream()
                .map(shop -> CompletableFuture.supplyAsync(() -> OfferService.getOfferNullable(shop, product))
                        .thenCompose(this::applyDiscountAsync) // NPE?!
                        .thenApply(Offer::getPrice) // NPE?!
                        .thenCombine(getRateAsync(shop.getCurrency(), targetCurrency),
                                (price, rate) -> Util.roundTo2DecimalPlaces(price * rate)))
                .collect(Collectors.toList());

        final OptionalDouble optAvg = Util.sequence(prices).get().stream()
                .mapToDouble(Double::doubleValue)
                .average();

        return optAvg.isPresent() ? Util.roundTo2DecimalPlaces(optAvg.getAsDouble()) : null; // :(
    }

    private CompletableFuture<Offer> getOfferNullableAsync(Shop shop, String product) {
        return CompletableFuture.supplyAsync(() -> OfferService.getOfferNullable(shop, product), executorService);
    }

    private CompletableFuture<Offer> getOfferWithExceptionAsync(Shop shop, String product) {
        return CompletableFuture.supplyAsync(() -> OfferService.getOfferWithException(shop, product), executorService);
    }

    private CompletableFuture<Offer> applyDiscountAsync(Offer offer) {
        return CompletableFuture.supplyAsync(() -> DiscountService.applyDiscount(offer), executorService);
    }

    private CompletableFuture<Double> getRateAsync(Currency from, Currency to) {
        return CompletableFuture.supplyAsync(() -> ExchangeRateService.getRate(from, to), executorService);
    }
}
