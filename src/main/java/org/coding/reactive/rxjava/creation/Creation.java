package org.coding.reactive.rxjava.creation;

import rx.Observable;

import java.util.concurrent.TimeUnit;

import static org.coding.reactive.rxjava.common.Utils.subscribePrint;

public class Creation {
    public static void main(String[] args) {
        Observable.just('R', 'x', 'J', 'a', 'v', 'a').subscribe(System.out::println);

        subscribePrint(Observable.interval(500L, TimeUnit.MILLISECONDS),
                "Interval Observable");

        subscribePrint(Observable.timer(0L, 1L, TimeUnit.SECONDS),
                "Timed Interval Observable");

        subscribePrint(Observable.timer(1L, TimeUnit.SECONDS),
                "Timer Observable");

        subscribePrint(Observable.error(new Exception("Test Error!")),
                "Error Observable");

        subscribePrint(Observable.empty(), "Empty Observable");
        subscribePrint(Observable.never(), "Never Observable");

        subscribePrint(Observable.range(1, 10), "Range Observable");

        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
        }

    }
}
