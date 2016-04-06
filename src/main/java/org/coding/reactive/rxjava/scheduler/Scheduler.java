package org.coding.reactive.rxjava.scheduler;

import rx.Observable;
import rx.schedulers.Schedulers;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.coding.reactive.rxjava.common.Utils.debugNotifications;
import static org.coding.reactive.rxjava.common.Utils.subscribePrint;

/**
 * Created by svante.kumlien on 06.04.16.
 */
public class Scheduler {

    public static void main(String[] args) {
        //singleThreaded();
        //defaultIntervalThread();
        //specificIntervalThread();
        //subscribeOn();
    }

    private static void subscribeOn() {
        final rx.Scheduler s1 = Schedulers.computation();
        final rx.Scheduler s2 = Schedulers.io();
        final Observable<Object> observable = Observable
                .create(o -> {
                    System.out.println(Thread.currentThread().getName() + "|calling onNext");
                    o.onNext(1L);
                    System.out.println(Thread.currentThread().getName() + "|calling onNext");
                    o.onNext(2L);
                    System.out.println(Thread.currentThread().getName() + "|calling onNext");
                    o.onNext(3L);
                    System.out.println(Thread.currentThread().getName() + "|calling onCompleted");
                    o.onCompleted();
                })
                .subscribeOn(s1)
                .observeOn(s2);

        subscribePrint(observable, "My observable");
        System.out.println(Thread.currentThread().getName() + "|done");
        sleep(2000);
    }

    private static void sleep(long millis) {
        try {
            System.out.println(Thread.currentThread().getName() + "|sleeping...");
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //Use default single threaded mode
    //Debug using doOnEach. See http://reactivex.io/documentation/operators/do.html
    private static void singleThreaded() {
        Observable
                .range(5, 5)
                .doOnEach(debugNotifications("Single thread"))
                .subscribe();
    }

    private static void defaultIntervalThread() {
        Observable
                .interval(250, MILLISECONDS)
                .take(5)
                .doOnEach(debugNotifications("Default interval thread"))
                .subscribe();
        sleep(2500);

    }

    private static void specificIntervalThread() {
        Observable
                .interval(250, MILLISECONDS, Schedulers.io())
                .take(5)
                .doOnEach(debugNotifications("Interval with io thread"))
                .subscribe();
        sleep(2500);
    }
}
