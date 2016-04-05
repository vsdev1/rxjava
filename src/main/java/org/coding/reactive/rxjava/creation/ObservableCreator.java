package org.coding.reactive.rxjava.creation;

import rx.Observable;
import rx.functions.Action1;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
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

    public Observable<Character> getJustObservable() {
        return Observable.just('R', 'x', 'J', 'a', 'v', 'a');
    }

    public Observable<String> getObservableFromList() {
        return  Observable.from(Arrays.asList("blue", "red", "green", "yellow", "orange", "cyan", "purple"));
    }

    public Observable<Integer> getObservableFromArray() {
        return Observable.from(new Integer[] {3, 5, 8});
    }

    public Observable<Integer> getRangeObservable() {
//        return Observable.empty();
        return Observable.range(5, 10);
    }
}
