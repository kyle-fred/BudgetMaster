package com.budgetmaster.utils.date;

import com.budgetmaster.test.constants.TestData;

import java.time.YearMonth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DateUtilsTest {
    // -- Get Valid Year Month Tests --
    @Test
    void getValidYearMonth_NullInput_ReturnsCurrentYearMonth() {
        YearMonth expected = YearMonth.now();
        YearMonth actual = DateUtils.getValidYearMonth(null);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void getValidYearMonth_EmptyInput_ReturnsCurrentYearMonth() {
        YearMonth expected = YearMonth.now();
        YearMonth actual = DateUtils.getValidYearMonth(TestData.CommonTestDataConstants.EMPTY_STRING);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void getValidYearMonth_ValidInput_ReturnsParsedYearMonth() {
        YearMonth expected = TestData.MonthTestDataConstants.MONTH_EXISTING;
        YearMonth actual = DateUtils.getValidYearMonth(TestData.MonthTestDataConstants.MONTH_STRING_EXISTING);
        assertEquals(expected, actual);
    }
}
