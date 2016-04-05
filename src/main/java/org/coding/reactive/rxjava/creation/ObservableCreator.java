package org.coding.reactive.rxjava.creation;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;

public class ObservableCreator {

    public Observable<Character> createJustObservable() {
        return Observable.just('R', 'x', 'J', 'a', 'v', 'a');
    }

    public Observable<String> createObservableFromList() {
        return Observable.from(Arrays.asList("blue", "red", "green", "yellow", "orange", "cyan", "purple"));
    }

    public Observable<Integer> createObservableFromArray() {
        return Observable.from(new Integer[] {3, 5, 8});
    }

    public Observable<Path> createObservableFromIterableDirectoryStream() throws IOException {
        Path resources = Paths.get("src", "main", "resources");
        DirectoryStream<Path> directoryStream = Files.newDirectoryStream(resources);

        return Observable.from(directoryStream);
    }

    public Observable<Long> createObservableWithIntervalOperator(Scheduler scheduler) throws IOException {
        return Observable.interval(100L, TimeUnit.MILLISECONDS, scheduler);
    }

    public Observable<Integer> createRangeObservable() {
//        return Observable.empty();
        return Observable.range(5, 10);
    }

    public Observable<String> createObservableWithCreateOperator() throws IOException {
        return createObservableFromIterable(Arrays.asList("blue", "red", "green", "yellow", "orange", "cyan", "purple"));
    }

    private <T> Observable<T> createObservableFromIterable(Iterable<T> iterable) throws IOException {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {
                    Iterator<T> iterator = iterable.iterator();

                    while (iterator.hasNext()) {
                        if (subscriber.isUnsubscribed()) {
                            return;
                        }
                        subscriber.onNext(iterator.next());
                    }

                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onCompleted();
                    }
                } catch (Exception e) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(e);
                    }
                }
            }
        });
    }

}
