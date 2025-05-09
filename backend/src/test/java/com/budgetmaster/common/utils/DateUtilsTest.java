package com.budgetmaster.common.utils;

import com.budgetmaster.test.constants.TestData.BudgetTestConstants;
import com.budgetmaster.test.constants.TestData.StringTestConstants;

import java.time.YearMonth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DateUtilsTest {
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
        YearMonth actual = DateUtils.getValidYearMonth(StringTestConstants.EMPTY_STRING);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void getValidYearMonth_ValidInput_ReturnsParsedYearMonth() {
        YearMonth expected = BudgetTestConstants.Default.YEAR_MONTH;
        YearMonth actual = DateUtils.getValidYearMonth(BudgetTestConstants.Default.YEAR_MONTH.toString());
        assertEquals(expected, actual);
    }
}
