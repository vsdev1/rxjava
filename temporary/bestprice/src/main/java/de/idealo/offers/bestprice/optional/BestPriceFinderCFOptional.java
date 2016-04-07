package de.idealo.offers.bestprice.optional;

import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import de.idealo.offers.bestprice.BestPriceFinderCF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.idealo.offers.bestprice.model.Currency;
import de.idealo.offers.bestprice.model.Offer;
import de.idealo.offers.bestprice.model.Shop;
import de.idealo.offers.bestprice.service.DiscountService;
import de.idealo.offers.bestprice.service.ExchangeRateService;
import de.idealo.offers.bestprice.service.OfferService;
import de.idealo.offers.bestprice.service.Util;

public class BestPriceFinderCFOptional implements IBestPriceFinderWithOptional {

    private static final Logger LOG = LoggerFactory.getLogger(BestPriceFinderCF.class);

    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Override
    public Optional<Double> getOptionalDiscountedPrice(final Shop shop, final String product) throws InterruptedException, ExecutionException {
        return getOptionalOfferAsync(shop, product) // async, returns CompletableFuture<Optional<Offer>>
                .thenApply(optionalOffer -> optionalOffer.map(DiscountService::applyDiscount)) // still async, type unchanged
                .get() // synchronous, returns Optional<Offer>
                .map(Offer::getPrice); // returns Optional<Double>
    }

    @Override
    public Optional<Double> getOptionalAveragePrice(String product, Currency targetCurrency) throws Exception {

        final List<CompletableFuture<Optional<Double>>> prices = Shop.SHOPS.stream()
                .map(shop -> getOptionalOfferAsync(shop, product) // returns CF<Optional<Offer>>
                        .thenCompose(this::applyDiscountToOptionalAsync) // another async call -> use compose for chaining them
                        .thenApply(optionalOffer -> optionalOffer.map(Offer::getPrice)) // map -> Optional remains Optional
                        .thenCombine(getRateAsync(shop.getCurrency(), targetCurrency),
                                (optionalPrice, rate) -> optionalPrice.map(price -> Util.roundTo2DecimalPlaces(price * rate))))
                .collect(Collectors.toList());

        final OptionalDouble optAvg = Util.sequence(prices).get().stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .mapToDouble(Double::doubleValue)
                .average();

        return optAvg.isPresent() ? Optional.of(Util.roundTo2DecimalPlaces(optAvg.getAsDouble())) : Optional.empty(); // :(
    }

    private CompletableFuture<Optional<Offer>> getOptionalOfferAsync(Shop shop, String product) {
        return CompletableFuture.supplyAsync(() -> OfferService.getOptionalOffer(shop, product), executorService);
    }

    private CompletableFuture<Optional<Offer>> applyDiscountToOptionalAsync(Optional<Offer> optionalOffer) {
        return CompletableFuture.supplyAsync(() -> optionalOffer.map(DiscountService::applyDiscount), executorService);
    }

    private CompletableFuture<Double> getRateAsync(Currency from, Currency to) {
        return CompletableFuture.supplyAsync(() -> ExchangeRateService.getRate(from, to), executorService);
    }
}
