package org.coding.reactive.rxjava.creation;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Scheduler;
import rx.observables.ConnectableObservable;

public class ColdAndHotObservables {

    public ConnectableObservable<Long> createConnectableObservable(Scheduler scheduler) {
        // TODO: create a hot observable that fires each 200 ms
        return Observable
                .interval(200, TimeUnit.MILLISECONDS, scheduler)
                .publish();
    }
}
