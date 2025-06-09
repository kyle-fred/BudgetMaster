package com.budgetmaster.application.util;

import java.time.YearMonth;

public class DateUtils {

  /**
   * Returns a valid YearMonth. Defaults to current YearMonth if input is null or empty. Assumes
   * input is already validated by DTO pattern validation.
   */
  public static YearMonth getValidYearMonth(String monthString) {
    if (monthString == null || monthString.isEmpty()) {
      return YearMonth.now();
    }
    return YearMonth.parse(monthString);
  }
}
