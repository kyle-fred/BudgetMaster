package com.budgetmaster.application.util;

import java.time.YearMonth;

import org.junit.jupiter.api.Test;

import com.budgetmaster.testsupport.constants.StringConstants;
import com.budgetmaster.testsupport.constants.domain.BudgetConstants;

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
        YearMonth actual = DateUtils.getValidYearMonth(StringConstants.EMPTY);
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
