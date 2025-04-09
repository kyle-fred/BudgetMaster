package com.budgetmaster.utils.date;

import java.time.YearMonth;

public class DateUtils {

    /**
     * Returns a valid YearMonth. Defaults to current YearMonth if input is null or empty.
     * Assumes input is already validated by DTO pattern validation.
     */
    public static YearMonth getValidYearMonth(String monthYearString) {
        if (monthYearString == null || monthYearString.isEmpty()) {
            return YearMonth.now();
        }
        return YearMonth.parse(monthYearString);
    }
}