package org.coding.reactive.rxjava.creation;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.apache.http.ObservableHttp;

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

    public Observable<String> createHttpGetObservable(String url) throws IOException {
        CloseableHttpAsyncClient client = HttpAsyncClients.createDefault();
        client.start();

        return ObservableHttp.createGet(url, client)
                .toObservable()
                .flatMap(resp -> resp.getContent()
                        .map(bytes -> new String(bytes, java.nio.charset.StandardCharsets.UTF_8)))
                .retry(5)
                .cast(String.class)
                .map(String::trim);
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
