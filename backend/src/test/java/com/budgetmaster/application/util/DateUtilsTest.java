package com.budgetmaster.application.util;

import java.time.YearMonth;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.budgetmaster.testsupport.constants.StringConstants;
import com.budgetmaster.testsupport.constants.domain.BudgetConstants;

import static org.assertj.core.api.Assertions.assertThat;

public class DateUtilsTest {

    private static YearMonth now;

    @BeforeAll
    static void setUp() {
        now = YearMonth.now();
    }

    @Test
    void getValidYearMonth_NullInput_ReturnsCurrentYearMonth() {
        YearMonth month = DateUtils.getValidYearMonth(null);

        assertThat(month).isNotNull();
        assertThat(month).isEqualTo(now);
    }

    @Test
    void getValidYearMonth_EmptyInput_ReturnsCurrentYearMonth() {
        YearMonth month = DateUtils.getValidYearMonth(StringConstants.EMPTY);

        assertThat(month).isNotNull();
        assertThat(month).isEqualTo(now);
    }

    @Test
    void getValidYearMonth_ValidInput_ReturnsParsedYearMonth() {
        YearMonth month = DateUtils.getValidYearMonth(BudgetConstants.Default.YEAR_MONTH.toString());

        assertThat(month).isEqualTo(BudgetConstants.Default.YEAR_MONTH);
    }
}
