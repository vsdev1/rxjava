package org.coding.reactive.rxjava.creation;

import rx.Observable;

public class Creation {
    public static void main(String[] args) {
        Observable.just('R', 'x', 'J', 'a', 'v', 'a').subscribe(System.out::println);
    }
}
