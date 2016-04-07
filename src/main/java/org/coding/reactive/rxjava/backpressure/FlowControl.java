package org.coding.reactive.rxjava.backpressure;

import rx.Observable;
import rx.Scheduler;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class FlowControl {

    public Observable<List<Integer>> createObservableWithBufferByCount() {
        // TODO: create observable from a range that buffers items
        return Observable.range(0, 10).buffer(4);
    }

    public Observable<List<Long>> createObservableWithBufferByTime(Scheduler scheduler) {
        return Observable.interval(100, TimeUnit.MILLISECONDS, scheduler).take(10)
                .buffer(250, TimeUnit.MILLISECONDS, scheduler);
    }

    public Observable<Long> createObservableWithSample(Scheduler scheduler) {
        return Observable.interval(150, TimeUnit.MILLISECONDS, scheduler)
                .sample(1, TimeUnit.SECONDS, scheduler)
                .take(3);
    }

    public Observable<List<Integer>> createObservableWithWindowByCount() {
        return Observable.range(0, 5)
                .window(3, 1)
                .flatMap(o -> o.toList());
    }
}
