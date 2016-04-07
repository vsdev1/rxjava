package org.coding.reactive.rxjava.errors;

import org.junit.Test;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 */
public class TestRetry {

    //Basic retry, try it out and then try the overloaded versions of retry.
    @Test
    public void testRetry() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(10);
        Observable.interval(5, TimeUnit.MILLISECONDS)
                .map(input -> {
                    if (Math.random() < .5) {
                        throw new RuntimeException();
                    }
                    return input;
                })
                .subscribeOn(Schedulers.newThread())
                .retry()
                .distinct()
                .subscribe(in -> {
                    System.out.println("Got a " + in.intValue());
                    latch.countDown();
                });

        latch.await();
    }



    //Pretty advanced retry...
    //This retries 3 times, each time incrementing the number of seconds it waits.
    //When you understand the code, try to modify it to only retry a specific type of Exception n number of times every second.
    @Test
    public void testRetryWhen() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Observable.interval(10, TimeUnit.MILLISECONDS)
                .map(input -> {
                    if (Math.random() < 1) {
                        throw new RuntimeException();
                    }
                    return input;
                })
                .subscribeOn(Schedulers.newThread())
                .retryWhen(attempts -> attempts.zipWith(
                        Observable.range(1,3), (attempt, errCount) -> errCount)
                            .flatMap(errCount -> {
                                System.out.println("Delay retry by " +errCount + " seconds");
                                return Observable.timer(errCount, TimeUnit.SECONDS);
                        }))
                .subscribe(
                        System.out::println,
                        e -> System.err.println(e),
                        () -> latch.countDown()); //release the latch onComplete
        latch.await();
    }


    //This retry goes on forever
    @Test
    public void testRetryWhenForever() throws InterruptedException {
        Observable.interval(100, TimeUnit.MILLISECONDS)
                .map(input -> {throw new RuntimeException();})
                .retryWhen(errorNotification ->
                        errorNotification.flatMap((Func1<Throwable, Observable<?>>) throwable -> {
                            System.out.println("Delay retry by 1 second");
                            return Observable.timer(1, TimeUnit.SECONDS);
                        }))
                .subscribe(
                        System.out::println,
                        e -> System.err.println(e)
                );

        Thread.sleep(5000);
    }

    //This retry goes on forever, make it retry only n number of times (using retryWhen).
    //If you have time, enhance even further, by also propagating the exception.
    //@Test
    public void testRetryWhenNTimes() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Observable.interval(100, TimeUnit.MILLISECONDS)
                .map(input -> {throw new RuntimeException();})
                .retryWhen(errorNotification ->
                        errorNotification.flatMap((Func1<Throwable, Observable<?>>) throwable -> Observable.timer(1, TimeUnit.SECONDS)))
                .subscribe(
                        System.out::println,
                        e -> System.err.println(e),
                        () -> latch.countDown()
                );

        latch.await();
    }
}
