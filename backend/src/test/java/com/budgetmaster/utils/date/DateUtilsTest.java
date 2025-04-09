package com.budgetmaster.utils.date;

import static org.junit.jupiter.api.Assertions.*;

import java.time.YearMonth;

import org.junit.jupiter.api.Test;

public class DateUtilsTest {

    @Test
    void testGetValidYearMonth_NullInput_ReturnsCurrentYearMonth() {
        YearMonth expected = YearMonth.now();
        YearMonth actual = DateUtils.getValidYearMonth(null);
        assertNotNull(actual);
        assertEquals(expected, actual, "Should return current YearMonth when input is null");
    }

    @Test
    void testGetValidYearMonth_EmptyInput_ReturnsCurrentYearMonth() {
        YearMonth expected = YearMonth.now();
        YearMonth actual = DateUtils.getValidYearMonth("");
        assertNotNull(actual);
        assertEquals(expected, actual, "Should return current YearMonth when input is empty");
    }

    @Test
    void testGetValidYearMonth_ValidInput_ReturnsParsedYearMonth() {
        YearMonth expected = YearMonth.of(2025, 3);
        YearMonth actual = DateUtils.getValidYearMonth("2025-03");
        assertEquals(expected, actual, "Should correctly parse and return the set YearMonth");
    }
}
