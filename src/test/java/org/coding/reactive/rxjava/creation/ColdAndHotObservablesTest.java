package org.coding.reactive.rxjava.creation;

import org.junit.Before;
import org.junit.Test;
import rx.Subscription;
import rx.observables.ConnectableObservable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class ColdAndHotObservablesTest {

    private ColdAndHotObservables coldAndHotObservables;

    @Before
    public void setUp() throws Exception {
        coldAndHotObservables = new ColdAndHotObservables();
    }

    @Test
    public void connectAndDisconnect() throws Exception {
        TestScheduler scheduler = Schedulers.test();
        TestSubscriber<Long> subscriber = new TestSubscriber<>();

        ConnectableObservable<Long> connectable = coldAndHotObservables.createConnectableObservable(scheduler);
        Subscription connection = connectable.connect();
        connectable.subscribe(subscriber);

        scheduler.advanceTimeBy(1000, TimeUnit.MILLISECONDS);
        subscriber.assertReceivedOnNext(Arrays.asList(0L, 1L, 2L, 3L, 4L));

        connection.unsubscribe();
        scheduler.advanceTimeBy(1000, TimeUnit.MILLISECONDS);
        subscriber.assertReceivedOnNext(Arrays.asList(0L, 1L, 2L, 3L, 4L));

        connection = connectable.connect();
        connectable.subscribe(subscriber);
        scheduler.advanceTimeBy(1000, TimeUnit.MILLISECONDS);
        subscriber.assertReceivedOnNext(Arrays.asList(0L, 1L, 2L, 3L, 4L, 0L, 1L, 2L, 3L, 4L));

        connection.unsubscribe();
    }
}