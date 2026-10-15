package com.an.campustaskapi.task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** First JUnit test: verify the test runner, without Spring or MySQL. */
class TaskSimpleTest {

    @Test
    void shouldAddTwoNumbers() {
        int result = 1 + 2;
        assertEquals(3, result);
    }
}
