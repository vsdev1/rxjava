package org.coding.reactive.rxjava.combination;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;

import rx.Observable;
import rx.Subscriber;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

public class ReactiveSum {

    private CountDownLatch latch = new CountDownLatch(1);

    public static void main(String[] args) {
        new ReactiveSum().run();
    }

    /**
     * To implement:
     * <ul>
     * <li>Runs in the terminal.</li>
     * <li>Once started, it will run until the user enters exit.</li>
     * <li>If the user enters a:<number>, the a collector will be updated to the <number>.</li>
     * <li>If the user enters b:<number>, the b collector will be updated to the <number>.</li>
     * <li>If the user enters anything else, it will be skipped.</li>
     * <li>When both the a and b collectors have initial values, their sum will automatically be computed and printed
     * on the standard output in the format a + b = <sum>. On every change in a or b, the sum will be updated and printed.</li>
     * </ul>
     */
    public void run() {
        ConnectableObservable<String> input = from(System.in);

        Observable<Double> a = varStream("a", input);
        Observable<Double> b = varStream("b", input);

        reactiveSum(a, b);

        input.connect();

        try {
            latch.await();
        } catch (InterruptedException e) {
        }
    }

    /**
     * Gets the the value of &lt;number&gt; of the input &lt;varName:&gt;&lt;number&gt;.
     */
    private Observable<Double> varStream(final String varName, Observable<String> input) {
        final Pattern pattern = Pattern.compile("\\s*" + varName
                + "\\s*[:|=]\\s*(-?\\d+\\.?\\d*)");

        return input
                .map(pattern::matcher)
                .filter(matcher -> matcher.matches()
                        && matcher.group(1) != null)
                .map(matcher -> matcher.group(1))
                .map(Double::parseDouble);
    }

    private void reactiveSum(Observable<Double> a, Observable<Double> b) {
        Observable
                .combineLatest(a.startWith(0.0), b.startWith(0.0), (x, y) -> x + y)
                .subscribeOn(Schedulers.io())
                .subscribe(
                        sum -> System.out.println("update : a + b = " + sum),
                        error -> {
                            System.out.println("Got an error!");
                            error.printStackTrace();
                        }, () -> {
                            System.out.println("Exiting...");
                            latch.countDown();
                        });

    }

    private ConnectableObservable<String> from(final InputStream stream) {
        return from(new BufferedReader(new InputStreamReader(stream)));
    }

    private ConnectableObservable<String> from(final BufferedReader reader) {
        return Observable.create((Subscriber<? super String> subscriber) -> {
            try {
                String line;

                if (subscriber.isUnsubscribed()) {
                    return;
                }

                while (!subscriber.isUnsubscribed() && (line = reader.readLine()) != null) {
                    if (line.equals("exit")) {
                        break;
                    }

                    subscriber.onNext(line);
                }
            } catch (IOException e) {
                subscriber.onError(e);
            }

            if (!subscriber.isUnsubscribed()) {
                subscriber.onCompleted();
            }
        }).publish();
    }
}
