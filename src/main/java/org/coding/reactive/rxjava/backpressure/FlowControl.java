package org.coding.reactive.rxjava.backpressure;

import rx.Observable;
import rx.Scheduler;

import java.util.List;

public class FlowControl {

    public Observable<List<Integer>> createObservableWithBufferByCount() {
        // TODO: create observable from a range of values that buffers items based on item count
        return Observable.empty();
    }

    public Observable<List<Long>> createObservableWithBufferByTime(Scheduler scheduler) {
        // TODO: create observable from a time interval that buffers items base on time
        return Observable.empty();
    }

    public Observable<Long> createObservableWithSample(Scheduler scheduler) {
        // TODO: create observable from a time interval that samples items base on time
        return Observable.empty();
    }

    public Observable<List<Integer>> createObservableWithWindowByCount() {
        // TODO: create observable from a range of values that windows items base on item count
        return Observable.empty();
    }
}
