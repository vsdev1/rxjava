package org.coding.reactive.rxjava.creation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ObservableCreationTest {
    private ObservableCreation observableCreation;

    @Before
    public void setUp() throws Exception {
        observableCreation = new ObservableCreation();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void trial() throws Exception {
        assertThat(true, is(equalTo(true)));
    }

}