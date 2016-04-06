package org.coding.reactive.rxjava.scheduler;

import org.coding.reactive.rxjava.common.Utils;
import rx.Observable;

/**
 * Created by svante.kumlien on 06.04.16.
 */
public class Scheduler {

    public static void main(String[] args) {
        singleThreaded();
    }

    private static void singleThreaded() {
        Observable
                .range(5,5)
                .doOnEach(Utils.debug("Single thread", ""))
                .subscribe();
    }
}
