package de.idealo.offers.bestprice.optional;

import de.idealo.offers.bestprice.model.Currency;
import de.idealo.offers.bestprice.model.Offer;
import de.idealo.offers.bestprice.model.Shop;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static java.time.Instant.now;
import static org.hamcrest.Matchers.isIn;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class BestPriceFinderWithOptionalTest {

    private static final Logger LOG = LoggerFactory.getLogger(BestPriceFinderWithOptionalTest.class);

    private IBestPriceFinderWithOptional bestPriceFinder;

    // better to use class name here
    // - more readable
    // - enables re-run of single test cases from test tree in IDEA "Run" view
    public BestPriceFinderWithOptionalTest(final String bestPriceFinderClassName) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        this.bestPriceFinder = (IBestPriceFinderWithOptional) Class.forName(BestPriceFinderWithOptionalTest.class.getPackage().getName() + "." + bestPriceFinderClassName)
                .newInstance();
    }

    @Parameters(name = "{0}")
    public static Object[][] params() {
        return new Object[][] { { "BestPriceFinderWithOptional" }, { "BestPriceFinderCFOptional" } };
    }

    @Test
    public void getOptionalDiscountedPrice_existingOfferHasPrice() throws Exception {
        timedRun(() -> {
            final Optional<Double> price = bestPriceFinder.getOptionalDiscountedPrice(Shop.MY_FAVORITE_SHOP, "iPad");
            assertThat(price.get(), is(closeTo(1400.0, 0.001)));
        });
    }

    @Test
    public void getOptionalDiscountedPrice_nonExistingOfferHasNoPrice() throws Exception {
        timedRun(() -> {
            final Optional<Double> price = bestPriceFinder.getOptionalDiscountedPrice(Shop.MY_FAVORITE_SHOP, "foobar");
            assertThat(price.isPresent(), is(false));
        });
    }

    @Test
    public void getOptionalAveragePrice_existingOfferInAllShopsHasAvg() throws Exception {
        timedRun(() -> {
            final Optional<Double> price = bestPriceFinder.getOptionalAveragePrice("iPad", Currency.USD);
            assertThat(price.get(), is(closeTo(504.82, 0.001)));
        });
    }

    @Test
    public void getOptionalAveragePrice_existingOfferInSomeShopsHasAvg() throws Exception {
        timedRun(() -> {
            final Optional<Double> price = bestPriceFinder.getOptionalAveragePrice("Samsung S6", Currency.USD);
            assertThat(price.get(), is(closeTo(628.50, 0.001)));
        });
    }

    @Test
    public void getOptionalAveragePrice_nonExistingOfferHasNoAvg() throws Exception {
        timedRun(() -> {
            final Optional<Double> price = bestPriceFinder.getOptionalAveragePrice("foobar", Currency.USD);
            assertThat(price.isPresent(), is(false));
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