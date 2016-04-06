package org.coding.reactive.rxjava.creation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class ObservableCreatorTest {

    static final Logger LOG = LoggerFactory.getLogger(ObservableCreatorTest.class);

    private ObservableCreator observableCreator;

    @Before
    public void setUp() throws Exception {
        observableCreator = new ObservableCreator();
    }

    @Test
    public void shouldCreateObservableWithJustOperator() throws Exception {
        final Observable<Character> observable = observableCreator.createJustObservable();

        assertThat(observable.toList().toBlocking().single(), contains('R', 'x', 'J', 'a', 'v', 'a'));
    }

    @Test
    public void shouldCreateObservableFromList() throws Exception {
        final Observable<String> observable = observableCreator.createObservableFromList();

        assertThat(observable.toList().toBlocking().single(), contains("blue", "red", "green", "yellow", "orange", "cyan", "purple"));
    }

    @Test
    public void shouldCreateObservableFromArray() throws Exception {
        final Observable<Integer> observable = observableCreator.createObservableFromArray();

        assertThat(observable.toList().toBlocking().single(), contains(3, 5, 8));
    }

    @Test
    public void shouldCreateObservableFromStream() throws Exception {
        final Observable<Integer> observable = observableCreator.createObservableFromStream();

        assertThat(observable.toList().toBlocking().single(), contains(3, 5, 8));
    }

    @Test
    public void shouldCreateObservableFromIterableDirectoryStream() throws Exception {
        final Observable<Path> observable = observableCreator.createObservableFromIterableDirectoryStream();

        final List<Path> single = observable.toList().toBlocking().single();
        assertThat(single.get(0).toString(), is(equalTo("src/main/resources/empty.properties")));
    }

    @Test
    public void shouldCreateObservableWithRangeOperator() throws Exception {
        final Observable<Integer> observable = observableCreator.createRangeObservable();

        assertThat(observable.toList().toBlocking().single(), contains(5, 6, 7, 8, 9, 10, 11, 12, 13, 14));
    }

    @Test
    public void shouldCreateObservablewithCreateOperator() throws Exception {
        final Observable<String> observable = observableCreator.createObservableWithCreateOperator();

        assertThat(observable.toList().toBlocking().single(), contains("blue", "red", "green", "yellow", "orange", "cyan", "purple"));
    }

    @Test
    public void shouldCreateObservableWithIntervalOperator() throws Exception {
        TestScheduler scheduler = Schedulers.test();
        Observable<Long> observable = observableCreator.createObservableWithIntervalOperator(scheduler);
        TestSubscriber<Long> subscriber = new TestSubscriber<>();
        observable.subscribe(subscriber);

        assertThat(subscriber.getOnNextEvents(), is(empty()));

        scheduler.advanceTimeBy(101L, TimeUnit.MILLISECONDS);
        assertThat(subscriber.getOnNextEvents(), contains(0L));

        scheduler.advanceTimeBy(101L, TimeUnit.MILLISECONDS);
        assertThat(subscriber.getOnNextEvents(), contains(0L, 1L));

        scheduler.advanceTimeTo(1L, TimeUnit.SECONDS);
        assertThat(subscriber.getOnNextEvents(), contains(0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L));
    }

    @Test
    @Ignore
    // temporarily ignored because the jsonplaceholder api is currently down
    public void shouldCreateHttpGetObservable() throws Exception {
        Observable<String> observable = observableCreator.createHttpGetObservable("http://jsonplaceholder.typicode.com/posts/1");

        final String response = observable.toBlocking().single();
        LOG.info("http response: {}", response);
        JSONAssert.assertEquals("{userId:1, id: 1}", response, false);
    }
}