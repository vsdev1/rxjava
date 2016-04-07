package org.coding.reactive.rxjava.errors;

import org.junit.Test;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 */
public class TestRetry {

    @Test
    public void testRetry() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(10);
        Observable.interval(10, TimeUnit.MILLISECONDS)
                .map(input -> {
                    if (Math.random() < .5) {
                        throw new RuntimeException();
                    }
                    return input;
                })
                .subscribeOn(Schedulers.newThread())
                .retry()
                .distinct()
                .subscribe(in -> {
                    System.out.println("Got a " + in.intValue());
                    latch.countDown();
                });

        latch.await();
    }

    @Test
    public void testRetryWhen() {
        Observable.just("Hello Observable error!")
                .flatMap(this::throwingMethod)
                .onErrorResumeNext(e -> Observable.just("Got an error, returning this observable instead..."))
                .subscribe(
                        (i) -> System.out.println("Subscriber onNext: " + i),
                        (e) -> System.err.println("Subscriber onError: " + e),
                        () -> System.out.println("Subscriber onComplete"));
    }

    private Observable throwingMethod(String input) {
        throw new RuntimeException("Throwing while handling '" + input + "'");
    }
}
