package org.coding.reactive.rxjava.common;

import rx.Notification;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public final class Utils {

	public static <T> Subscription subscribePrint(Observable<T> observable, String name) {
		return observable.subscribe(
				(v) -> System.out.println(Thread.currentThread().getName()
						+ "|" + name + " : " + v), (e) -> {
					System.err.println("Error from " + name + ":");
					System.err.println(e);
					System.err.println(Arrays
							.stream(e.getStackTrace())
							.limit(5L)
							.map(stackEl -> "  " + stackEl)
							.collect(Collectors.joining("\n"))
							);
				}, () -> System.out.println(Thread.currentThread().getName() + "|" + name + " ended!"));
	}

    /**
     * Util method used to debugNotifications thread name and value for notifications from an {@link Observable}
     *
     * @param description
     * @param offset
     * @param <T>
     * @return A lambda logging all notifications using the specified description.
     */
    public static <T> Action1<Notification<? super T>> debugNotifications(String description, String offset) {
        AtomicReference<String> nextOffset = new AtomicReference<String>(">");
        return (Notification<? super T> notification) -> {
            switch (notification.getKind()) {
                case OnNext:
                    System.out.println(
                            Thread.currentThread().getName() +
                                    "|" + description + ": " + offset +
                                    nextOffset.get() + notification.getValue()
                    );
                    break;
                case OnError:
                    System.err.println(
                            Thread.currentThread().getName() +
                                    "|" + description + ": " + offset +
                                    nextOffset.get() + " X " + notification.getThrowable()
                    );
                    break;
                case OnCompleted:
                    System.out.println(
                            Thread.currentThread().getName() +
                                    "|" + description + ": " + offset +
                                    nextOffset.get() + "|"
                    );
                default:
                    break;
            }
            nextOffset.getAndUpdate(p -> "-" + p);
        };
    }

    public static <T> Action1<Notification<? super T>> debugNotifications(String description) {
        return debugNotifications(description, "");
    }

}