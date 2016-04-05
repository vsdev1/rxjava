package org.coding.reactive.rxjava.creation;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ObservableCreator {

    public static Action1<Throwable> NE = (e) -> {};

    public static void main(String[] args) {
        // from(list)
        List<String> list = Arrays.asList("blue", "red", "green", "yellow",
                "orange", "cyan", "purple");

        Observable<String> listObservable = Observable.from(list);
        listObservable.subscribe(System.out::println);
        listObservable.subscribe(color -> System.out.print(color + "|"), NE,
                System.out::println);
        listObservable.subscribe(color -> System.out.print(color + "/"), NE,
                System.out::println);

        // from(Iterable)
        Path resources = Paths.get("src", "main", "resources");
        try (DirectoryStream<Path> dStream = Files.newDirectoryStream(resources)) {
            Observable<Path> dirObservable = Observable.from(dStream);
            dirObservable.subscribe(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // from(array)
        Observable<Integer> arrayObservable = Observable.from(new Integer[] {
                3, 5, 8 });
        arrayObservable.subscribe(System.out::println);

    }

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

    public Observable<String> createObservableWithCreateOperator() throws IOException {
        return createObservableFromIterable(Arrays.asList("blue", "red", "green", "yellow", "orange", "cyan", "purple"));
    }

    public Observable<Integer> createRangeObservable() {
//        return Observable.empty();
        return Observable.range(5, 10);
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
