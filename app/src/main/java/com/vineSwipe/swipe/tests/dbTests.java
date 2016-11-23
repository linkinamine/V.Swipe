package com.vineSwipe.swipe.tests;

import android.test.InstrumentationTestCase;

/**
 * Created by Mohamed El Amine on 10/17/2016.
 */
public class dbTests extends InstrumentationTestCase {

    public void test() throws Exception {
        final int expected = 1;
        final int reality = 5;
        assertEquals(expected, reality);
    }
}
