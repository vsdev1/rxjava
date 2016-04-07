package org.coding.reactive.rxjava.errors;

import org.junit.Test;

import rx.Observable;

/**
 */
public class TestHandleInChain {

    @Test
    public void testOnErrorReturn() {
        Observable.just("Hello Observable error!")
                .flatMap(this::throwingMethod)
                .onErrorReturn(e -> "ERROR_TOKEN, pls handle me...")
                .subscribe(
                        (i) -> System.out.println("Subscriber onNext: " + i),
                        (e) -> System.err.println("Subscriber onError: " + e),
                        () -> System.out.println("Subscriber onComplete"));
    }

    @Test
    public void testOnErrorResumeNext() {
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
