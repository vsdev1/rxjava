package org.coding.reactive.rxjava.bestprice;

import static java.time.Instant.now;
import static org.hamcrest.Matchers.isIn;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.assertThat;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.coding.reactive.rxjava.bestprice.model.Currency;
import org.coding.reactive.rxjava.bestprice.model.Offer;
import org.coding.reactive.rxjava.bestprice.model.Shop;
import org.coding.reactive.rxjava.bestprice.service.NotFoundException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

@Ignore
public class BestPriceFinderTest {

    private static final Logger LOG = LoggerFactory.getLogger(BestPriceFinderTest.class);

    private BestPriceFinder bestPriceFinder;

    @Before
    public void setUp() throws Exception {
        this.bestPriceFinder = new BestPriceFinder();
    }

    @Test
    public void getDiscountedPriceNullable_existingOfferHasPrice() throws Exception {
        timedRun(() -> {
            final Double price = bestPriceFinder.getDiscountedPriceNullable(Shop.MY_FAVORITE_SHOP, "iPad");
            assertThat(price, is(closeTo(1400.0, 0.001)));
        });
    }

    @Test
    public void getDiscountedPriceNullable_nonExistingOfferIsNull() throws Exception {
        timedRun(() -> {
            final Double price = bestPriceFinder.getDiscountedPriceNullable(Shop.MY_FAVORITE_SHOP, "foobar");
            assertThat(price, is(nullValue()));
        });
    }

    @Test
    public void getDiscountedPriceWithException_existingOfferHasPrice() throws Exception {
        timedRun(() -> {
            // don't worry about currencies here ;)
            final Double price = bestPriceFinder.getDiscountedPriceWithException(Shop.SHOP_BEST_PRICE, "Nexus 6");
            assertThat(price, is(closeTo(320.0, 0.001)));
        });
    }


    @Test(expected = NotFoundException.class)
    public void getDiscountedPriceWithException_nonExistingOfferIsException() throws Exception {
        timedRun(() -> bestPriceFinder.getDiscountedPriceWithException(Shop.MY_FAVORITE_SHOP, "foobar"));
    }

    @Test
    public void findOffers_allShops() throws Exception {
        timedRun(() -> {
            final List<Offer> offers = bestPriceFinder.findOffers("iPad", Currency.EUR);
            LOG.info("*** Offers found ***\n{}", offers.stream().map(Offer::toString).collect(Collectors.joining("\n")));
            assertPricesMatch(offers, Arrays.asList(400.0, 365.62, 79.45, 387.78, 631.52), Currency.EUR);
        });
    }

    @Test
    public void findOffers_someShops() throws Exception {
        timedRun(() -> {
            final List<Offer> offers = bestPriceFinder.findOffers("Samsung S6", Currency.EUR);
            LOG.info("*** Offers found ***\n{}", offers.stream().map(Offer::toString).collect(Collectors.joining("\n")));
            assertPricesMatch(offers, Arrays.asList(279.20, 517.04, 596.44), Currency.EUR);
        });
    }

    @Test
    public void findOffers_noShops() throws Exception {
        timedRun(() -> {
            final List<Offer> offers = bestPriceFinder.findOffers("foobar", Currency.EUR);
            assertThat(offers, is(empty()));
        });
    }

    @Test
    public void getAveragePriceNullable_existingOfferInAllShopsHasAvg() throws Exception {
        timedRun(() -> {
            final Double price = bestPriceFinder.getAveragePriceNullable("iPad", Currency.USD);
            assertThat(price, is(closeTo(504.82, 0.001)));
        });
    }

    @Test
    public void getAveragePriceNullable_existingOfferInSomeShopsHasAvg() throws Exception {
        timedRun(() -> {
            final Double price = bestPriceFinder.getAveragePriceNullable("Samsung S6", Currency.USD);
            assertThat(price, is(closeTo(628.50, 0.001)));
        });
    }

    @Test
    public void getAveragePriceNullable_nonExistingOfferIsNull() throws Exception {
        timedRun(() -> {
            final Double price = bestPriceFinder.getAveragePriceNullable("foobar", Currency.USD);
            assertThat(price, is(nullValue()));
        });
    }

    private void timedRun(RunnableWithException test) throws Exception {
        MDC.put("impl", bestPriceFinder.getClass().getSimpleName());
        Instant start = now();
        try {
            test.run();
        } finally {
            LOG.info("Test took {}ms", ChronoUnit.MILLIS.between(start, now()));
        }
        MDC.remove("impl");
    }

    public interface RunnableWithException {
        void run() throws Exception;
    }

    private void assertPricesMatch(List<Offer> offers, List<Double> expectedPrices, Currency expectedCurrency) {
        Assert.assertThat(offers.size(), is(expectedPrices.size()));
        offers.forEach(o -> {
            Assert.assertThat(o.getCurrency(), is(expectedCurrency));
            Assert.assertThat(o.getPrice(), isIn(expectedPrices));
        });
    }
}