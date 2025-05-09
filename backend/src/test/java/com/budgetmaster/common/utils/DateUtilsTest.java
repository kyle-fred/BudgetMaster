package com.budgetmaster.common.utils;

import java.time.YearMonth;

import org.junit.jupiter.api.Test;

import com.budgetmaster.testsupport.budget.constants.BudgetConstants;
import com.budgetmaster.testsupport.constants.Strings;

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
        YearMonth actual = DateUtils.getValidYearMonth(Strings.EMPTY_STRING);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void getValidYearMonth_ValidInput_ReturnsParsedYearMonth() {
        YearMonth expected = BudgetConstants.Default.YEAR_MONTH;
        YearMonth actual = DateUtils.getValidYearMonth(BudgetConstants.Default.YEAR_MONTH.toString());
        assertEquals(expected, actual);
    }
}
