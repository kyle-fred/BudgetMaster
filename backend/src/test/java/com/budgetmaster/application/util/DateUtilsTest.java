package com.budgetmaster.application.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.YearMonth;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.budgetmaster.testsupport.constants.StringConstants;
import com.budgetmaster.testsupport.constants.domain.BudgetConstants;

@DisplayName("Date Utils Tests")
class DateUtilsTest {

  private static YearMonth now;

  @BeforeAll
  static void setUp() {
    now = YearMonth.now();
  }

  @Nested
  @DisplayName("Get Valid Year Month Operations")
  class GetValidYearMonthOperations {

    @Test
    @DisplayName("Should return current year month when input is null")
    void getValidYearMonth_withNullInput_returnsCurrentYearMonth() {
      YearMonth month = DateUtils.getValidYearMonth(null);

      assertThat(month).isNotNull();
      assertThat(month).isEqualTo(now);
    }

    @Test
    @DisplayName("Should return current year month when input is empty")
    void getValidYearMonth_withEmptyInput_returnsCurrentYearMonth() {
      YearMonth month = DateUtils.getValidYearMonth(StringConstants.EMPTY);

      assertThat(month).isNotNull();
      assertThat(month).isEqualTo(now);
    }

    @Test
    @DisplayName("Should return parsed year month when input is valid")
    void getValidYearMonth_withValidInput_returnsParsedYearMonth() {
      YearMonth month = DateUtils.getValidYearMonth(BudgetConstants.Default.YEAR_MONTH.toString());

      assertThat(month).isEqualTo(BudgetConstants.Default.YEAR_MONTH);
    }
  }
}
