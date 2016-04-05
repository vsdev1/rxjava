package org.coding.reactive.rxjava.creation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import rx.Observable;

public class ObservableCreationTest {
    private ObservableCreator observableCreator;

    @Before
    public void setUp() throws Exception {
        observableCreator = new ObservableCreator();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void shouldReturnJustObservable() throws Exception {
        final Observable<Character> rangeObservable = observableCreator.getJustObservable();

        assertThat(rangeObservable.toList().toBlocking().single(), contains('R', 'x', 'J', 'a', 'v', 'a'));
    }

    @Test
    public void shouldReturnRangeObservable() throws Exception {
        final Observable<Integer> rangeObservable = observableCreator.getRangeObservable();

        assertThat(rangeObservable.toList().toBlocking().single(), contains(5, 6, 7, 8, 9, 10, 11, 12, 13, 14));
    }
}