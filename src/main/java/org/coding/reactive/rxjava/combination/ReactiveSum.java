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

	public static Observable<Double> varStream(final String varName,
			Observable<String> input) {
		final Pattern pattern = Pattern.compile("\\s*" + varName
				+ "\\s*[:|=]\\s*(-?\\d+\\.?\\d*)");

		return input
				.map(pattern::matcher)
				.filter(matcher -> matcher.matches()
						&& matcher.group(1) != null)
				.map(matcher -> matcher.group(1))
				.map(Double::parseDouble);
	}

	public void reactiveSum(Observable<Double> a, Observable<Double> b) {
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

	public void run() {
		ConnectableObservable<String> input = from(System.in);

		Observable<Double> a = varStream("a", input);
		Observable<Double> b = varStream("b", input);

		reactiveSum(a, b);
		
		input.connect();

		try {
			latch.await();
		} catch (InterruptedException e) {}
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

    public static void main(String[] args) {
		new ReactiveSum().run();
	}
}
