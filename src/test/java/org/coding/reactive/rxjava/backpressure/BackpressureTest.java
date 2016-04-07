package org.coding.reactive.rxjava.backpressure;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class BackpressureTest {

    @Test
    public void shouldThrowMissingBackpressureException() {
        TestScheduler scheduler = Schedulers.test();
        TestSubscriber<Long> tester = new TestSubscriber<Long>() {
            @Override
            public void onNext(Long t) {
                scheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
                super.onNext(t);
            }
        };

        Observable.interval(1, TimeUnit.MILLISECONDS, scheduler)
                .observeOn(scheduler)
                .subscribe(tester);

        scheduler.advanceTimeBy(10, TimeUnit.MILLISECONDS);
        assertThat(
                tester.getOnErrorEvents().get(0),
                instanceOf(rx.exceptions.MissingBackpressureException.class));
    }
}