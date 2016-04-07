package org.coding.reactive.rxjava.backpressure;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class FlowControlTest {

    private FlowControl flowControl;

    @Before
    public void setUp() throws Exception {
        flowControl = new FlowControl();
    }

    @Test
    public void shouldBufferByCount() throws Exception {
        TestSubscriber<List<Integer>> tester = new TestSubscriber<>();

        flowControl.createObservableWithBufferByCount().subscribe(tester);

        tester.assertReceivedOnNext(Arrays.asList(
                Arrays.asList(0, 1, 2, 3),
                Arrays.asList(4, 5, 6, 7),
                Arrays.asList(8, 9)
        ));
        tester.assertTerminalEvent();
        tester.assertNoErrors();
    }

    @Test
    public void shouldBufferByTime() throws Exception {
        TestSubscriber<List<Long>> tester = new TestSubscriber<>();
        TestScheduler scheduler = Schedulers.test();

        flowControl.createObservableWithBufferByTime(scheduler).subscribe(tester);

        scheduler.advanceTimeBy(1000, TimeUnit.MILLISECONDS);

        tester.assertReceivedOnNext(Arrays.asList(
                Arrays.asList(0L, 1L),
                Arrays.asList(2L, 3L),
                Arrays.asList(4L, 5L, 6L),
                Arrays.asList(7L, 8L),
                Arrays.asList(9L)
        ));
        tester.assertTerminalEvent();
        tester.assertNoErrors();
    }

    @Test
    public void shouldSample() throws Exception {
        TestScheduler scheduler = Schedulers.test();
        TestSubscriber<Long> tester = new TestSubscriber<>();

        flowControl.createObservableWithSample(scheduler).subscribe(tester);

        scheduler.advanceTimeBy(3, TimeUnit.SECONDS);
        tester.assertReceivedOnNext(Arrays.asList(5L, 12L, 18L));
        tester.assertTerminalEvent();
        tester.assertNoErrors();
    }

    @Test
    public void shouldWindowByCount() {
        TestSubscriber<List<Integer>> tester = new TestSubscriber<>();

        flowControl.createObservableWithWindowByCount().subscribe(tester);

        tester.assertReceivedOnNext(Arrays.asList(
                Arrays.asList(0, 1, 2),
                Arrays.asList(1, 2, 3),
                Arrays.asList(2, 3, 4),
                Arrays.asList(3, 4),
                Arrays.asList(4)
        ));
    }

}