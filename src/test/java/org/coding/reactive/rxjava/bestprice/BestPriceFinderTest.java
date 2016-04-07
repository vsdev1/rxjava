package org.coding.reactive.rxjava.bestprice;

import org.coding.reactive.rxjava.bestprice.model.Currency;
import org.coding.reactive.rxjava.bestprice.model.Offer;
import org.coding.reactive.rxjava.bestprice.model.Shop;
import org.coding.reactive.rxjava.bestprice.service.NotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.observers.TestSubscriber;

import java.util.Arrays;
import java.util.List;

import static org.coding.reactive.rxjava.common.Utils.subscribePrint;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isIn;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.assertThat;

public class BestPriceFinderTest {

    private static final Logger LOG = LoggerFactory.getLogger(BestPriceFinderTest.class);

    private BestPriceFinder bestPriceFinder;

    @Before
    public void setUp() throws Exception {
        this.bestPriceFinder = new BestPriceFinder();
    }

    @Test
    public void getDiscountedPriceNullable_existingOfferHasPrice() throws Exception {
        Observable<Double> discountedPriceObservable = bestPriceFinder.getDiscountedPriceNullable(Shop.MY_FAVORITE_SHOP, "iPad");
        TestSubscriber<Double> subscriber = new TestSubscriber<>();
        discountedPriceObservable.subscribe(subscriber);

        subscribePrint(discountedPriceObservable, "getDiscountedPriceNullable_existingOfferHasPrice()");

        List<Double> prices = subscriber.getOnNextEvents();
        assertThat(prices.size(), is(equalTo(1)));
        assertThat(prices.get(0), is(closeTo(1400.0, 0.01)));
    }

    @Test
    public void getDiscountedPriceNullable_nonExistingOfferIsEmptyObservable() throws Exception {
        Observable<Double> discountedPriceObservable = bestPriceFinder.getDiscountedPriceNullable(Shop.MY_FAVORITE_SHOP, "foobar");
        TestSubscriber<Double> subscriber = new TestSubscriber<>();
        discountedPriceObservable.subscribe(subscriber);

        subscribePrint(discountedPriceObservable, "getDiscountedPriceNullable_nonExistingOfferIsNull()");

        List<Double> prices = subscriber.getOnNextEvents();
        assertThat(prices, is(empty()));
    }

    @Test
    public void getDiscountedPriceWithException_existingOfferHasPrice() throws Exception {
        Observable<Double> discountedPriceObservable = bestPriceFinder.getDiscountedPriceWithException(Shop.SHOP_BEST_PRICE, "Nexus 6");
        TestSubscriber<Double> subscriber = new TestSubscriber<>();
        discountedPriceObservable.subscribe(subscriber);

        subscribePrint(discountedPriceObservable, "getDiscountedPriceWithException_existingOfferHasPrice()");

        List<Double> prices = subscriber.getOnNextEvents();
        assertThat(prices.size(), is(equalTo(1)));
        assertThat(prices.get(0), is(closeTo(320.0, 0.01)));
    }


    @Test
    public void getDiscountedPriceWithException_nonExistingOfferIsException() throws Exception {
        Observable<Double> discountedPriceObservable = bestPriceFinder.getDiscountedPriceWithException(Shop.MY_FAVORITE_SHOP, "foobar");
        TestSubscriber<Double> subscriber = new TestSubscriber<>();
        discountedPriceObservable.subscribe(subscriber);

        subscribePrint(discountedPriceObservable, "getDiscountedPriceWithException_nonExistingOfferIsException()");

        assertThat(subscriber.getOnErrorEvents().get(0), instanceOf(NotFoundException.class));
    }

    @Test
    public void findOffers_allShops() throws Exception {
        Observable<Offer> offersObservable = bestPriceFinder.findOffers("iPad", Currency.EUR);
        TestSubscriber<Offer> subscriber = new TestSubscriber<>();
        offersObservable.subscribe(subscriber);

        subscribePrint(offersObservable, "findOffers_allShops()");

        assertPricesMatch(subscriber, Arrays.asList(400.0, 365.62, 79.45, 387.78, 631.52), Currency.EUR);
    }

    @Test
    public void findOffers_someShops() throws Exception {
        Observable<Offer> offersObservable = bestPriceFinder.findOffers("Samsung S6", Currency.EUR);
        TestSubscriber<Offer> subscriber = new TestSubscriber<>();
        offersObservable.subscribe(subscriber);

        subscribePrint(offersObservable, "findOffers_someShops()");

        assertPricesMatch(subscriber, Arrays.asList(279.20, 517.04, 596.44), Currency.EUR);
    }

    @Test
    public void findOffers_noShops() throws Exception {
        Observable<Offer> offersObservable = bestPriceFinder.findOffers("foobar", Currency.EUR);
        TestSubscriber<Offer> subscriber = new TestSubscriber<>();
        offersObservable.subscribe(subscriber);

        subscribePrint(offersObservable, "findOffers_noShops()");

        List<Offer> offers = subscriber.getOnNextEvents();
        assertThat(offers, is(empty()));
    }

    @Test
    public void getAveragePriceNullable_existingOfferInAllShopsHasAvg() throws Exception {
        Observable<Double> averagePriceObservable = bestPriceFinder.getAveragePriceNullable("iPad", Currency.USD);
        TestSubscriber<Double> subscriber = new TestSubscriber();
        averagePriceObservable.subscribe(subscriber);

        subscribePrint(averagePriceObservable, "getAveragePriceNullable_existingOfferInAllShopsHasAvg()");

        List<Double> averagePrices = subscriber.getOnNextEvents();
        assertThat(averagePrices.size(), is(equalTo(1)));
        assertThat(averagePrices.get(0), is(closeTo(504.82, 0.01)));
    }

    @Test
    public void getAveragePriceNullable_existingOfferInSomeShopsHasAvg() throws Exception {
        Observable<Double> averagePriceObservable = bestPriceFinder.getAveragePriceNullable("Samsung S6", Currency.USD);
        TestSubscriber<Double> subscriber = new TestSubscriber();
        averagePriceObservable.subscribe(subscriber);

        subscribePrint(averagePriceObservable, "getAveragePriceNullable_existingOfferInSomeShopsHasAvg()");

        List<Double> averagePrices = subscriber.getOnNextEvents();
        assertThat(averagePrices.size(), is(equalTo(1)));
        assertThat(averagePrices.get(0), is(closeTo(628.50, 0.01)));
    }

    @Test
    public void getAveragePriceNullable_nonExistingOfferIsEmptyObservable() throws Exception {
        Observable<Double> averagePriceObservable = bestPriceFinder.getAveragePriceNullable("foobar", Currency.USD);
        TestSubscriber<Double> subscriber = new TestSubscriber();
        averagePriceObservable.subscribe(subscriber);

        subscribePrint(averagePriceObservable, "getAveragePriceNullable_nonExistingOfferIsNull()");

        List<Double> averagePrices = subscriber.getOnNextEvents();
        assertThat(averagePrices, is(empty()));
    }

    private void assertPricesMatch(TestSubscriber<Offer> subscriber, List<Double> expectedPrices, Currency expectedCurrency) {
        List<Offer> offers = subscriber.getOnNextEvents();

        assertThat(offers.size(), is(equalTo(expectedPrices.size())));
        offers.forEach(offer -> {
            assertThat(offer.getCurrency(), is(expectedCurrency));
            assertThat(offer.getPrice(), isIn(expectedPrices));
        });
    }
}