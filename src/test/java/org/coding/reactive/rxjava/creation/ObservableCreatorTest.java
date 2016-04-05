package org.coding.reactive.rxjava.creation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import rx.Observable;

import java.nio.file.Path;
import java.util.List;

public class ObservableCreatorTest {
    private ObservableCreator observableCreator;

    @Before
    public void setUp() throws Exception {
        observableCreator = new ObservableCreator();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void shouldCreateObservableWithJustOperator() throws Exception {
        final Observable<Character> observable = observableCreator.createJustObservable();

        assertThat(observable.toList().toBlocking().single(), contains('R', 'x', 'J', 'a', 'v', 'a'));
    }

    @Test
    public void shouldCreateObservableFromList() throws Exception {
        final Observable<String> observable = observableCreator.createObservableFromList();

        assertThat(observable.toList().toBlocking().single(), contains("blue", "red", "green", "yellow", "orange", "cyan", "purple"));
    }

    @Test
    public void shouldCreateObservableFromArray() throws Exception {
        final Observable<Integer> observable = observableCreator.createObservableFromArray();

        assertThat(observable.toList().toBlocking().single(), contains(3, 5, 8));
    }

    @Test
    public void shouldCreateObservableFromIterableDirectoryStream() throws Exception {
        final Observable<Path> observable = observableCreator.createObservableFromIterableDirectoryStream();

        final List<Path> single = observable.toList().toBlocking().single();
        assertThat(single.get(0).toString(), is(equalTo("src/main/resources/empty.properties")));
    }

    @Test
    public void shouldCreateObservableWithRangeOperator() throws Exception {
        final Observable<Integer> observable = observableCreator.createRangeObservable();

        assertThat(observable.toList().toBlocking().single(), contains(5, 6, 7, 8, 9, 10, 11, 12, 13, 14));
    }

    @Test
    public void shouldCreateObservablewithCreateOperator() throws Exception {
        final Observable<String> observable = observableCreator.createObservableWithCreateOperator();

        assertThat(observable.toList().toBlocking().single(), contains("blue", "red", "green", "yellow", "orange", "cyan", "purple"));
    }

}