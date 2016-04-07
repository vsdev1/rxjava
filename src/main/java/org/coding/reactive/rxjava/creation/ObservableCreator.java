package org.coding.reactive.rxjava.creation;

import rx.Observable;
import rx.Scheduler;

import java.io.IOException;
import java.nio.file.Path;

public class ObservableCreator {

    public Observable<Character> createJustObservable() {
        // TODO: create observable from multiple single values
        return Observable.empty();
    }

    public Observable<String> createObservableFromList() {
        // TODO: create observable from list
        return Observable.empty();
    }

    public Observable<Integer> createObservableFromArray() {
        // TODO: create observable from array
        return Observable.empty();
    }

    public Observable<Integer> createObservableFromStream() {
        // TODO: create observable from stream
        return Observable.empty();
    }

    public Observable<Path> createObservableFromIterableDirectoryStream() throws IOException {
        // TODO: create observable from that emits all files in src/main/resources
        return Observable.empty();
    }

    public Observable<Long> createObservableWithIntervalOperator(Scheduler scheduler) throws IOException {
        // TODO: create observable from interval
        return Observable.empty();
    }

    public Observable<Integer> createRangeObservable() {
        // TODO: create observable from range
        return Observable.empty();
    }

    public Observable<String> createObservableWithCreateOperator() throws IOException {
        // TODO: create observable from list with Observable.create()
        return Observable.empty();
    }

    public Observable<String> createHttpGetObservable(String url) throws IOException {
        // TODO (advanced stuff): create observable that gets remote URL via http.
        // use https://github.com/ReactiveX/RxApacheHttp
        return Observable.empty();
    }
}
