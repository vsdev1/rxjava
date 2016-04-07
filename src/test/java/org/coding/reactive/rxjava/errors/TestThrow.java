package org.coding.reactive.rxjava.errors;

import org.junit.Test;

import rx.Observable;
import rx.exceptions.Exceptions;

/**
 *
 */
public class TestThrow {

    @Test
    public void testPropagate() {
        Observable.just("Hello propagate error!")
                .map(input -> {
                    try {
                        return throwingMethod(input);
                    } catch (Throwable t) {
                        throw Exceptions.propagate(t);
                    }
                })
                .doOnEach(s -> System.out.println("In doOnEach: " + s))
                .subscribe(
                        System.out::println,
                        (e) -> System.err.println("Got an exception in the subscriber: " + e.getMessage()));
    }

    @Test
    public void testObservableError() {
        Observable.just("Hello Observable error!")
                .flatMap(input -> {
                    try {
                        return throwingMethod(input);
                    } catch (Throwable t) {
                        return Observable.error(t);
                    }
                }).doOnEach(s -> System.out.println("In doOnEach: " + s))
        .subscribe(
                System.out::print,
                (e) -> System.err.println("Got an exception in the subscriber: " + e)
        );
    }

    private Observable throwingMethod(String input) {
        throw new RuntimeException("Throwing while handling '" + input + "'");
    }

}
