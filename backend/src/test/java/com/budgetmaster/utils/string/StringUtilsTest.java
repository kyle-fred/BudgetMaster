package com.budgetmaster.utils.string;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class StringUtilsTest {

    @Test
    void testCapitalize_WithValidString() {
        String input = "test string";
        String expected = "TEST STRING";
        assertEquals(expected, StringUtils.capitalize(input));
    }

    @Test
    void testCapitalize_WithNull() {
        assertNull(StringUtils.capitalize(null));
    }

    @Test
    void testCapitalize_WithEmptyString() {
        assertEquals("", StringUtils.capitalize(""));
    }
} 