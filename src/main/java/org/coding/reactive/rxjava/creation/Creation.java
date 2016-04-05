package org.coding.reactive.rxjava.creation;

import rx.Observable;

public class Creation {
    public static void main(String[] args) {
        Observable.just('S').subscribe(System.out::println);
    }
}
