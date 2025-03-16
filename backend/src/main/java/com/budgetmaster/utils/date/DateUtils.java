package com.budgetmaster.utils.date;

import java.time.YearMonth;
import com.budgetmaster.exception.InvalidMonthYearExceptionHandler;

public class DateUtils {

    /**
     * Returns a valid YearMonth. Defaults to current YearMonth if input is null or empty.
     */
    public static YearMonth getValidYearMonth(String monthYearString) {
        if (monthYearString == null || monthYearString.isEmpty()) {
            return YearMonth.now();
        }
        try {
            return parseYearMonth(monthYearString);
        } catch (IllegalArgumentException e) {
            throw new InvalidMonthYearExceptionHandler("Invalid month value. Month must be between 01 and 12. Expected format: YYYY-MM.");
        }
    }

    /**
     * Parses a string to YearMonth format.
     */
    public static YearMonth parseYearMonth(String yearMonthString) {
        try {
            return YearMonth.parse(yearMonthString);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid YearMonth value. Expected format: YYYY-MM.", e);
        }
    }
}
