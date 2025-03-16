package com.budgetmaster.utils.date;

import static org.junit.jupiter.api.Assertions.*;

import java.time.YearMonth;

import org.junit.jupiter.api.Test;

import com.budgetmaster.exception.InvalidMonthYearExceptionHandler;

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

    @Test
    void testGetValidYearMonth_InvalidFormat_ThrowsException() {
        assertThrows(InvalidMonthYearExceptionHandler.class, () -> {
            DateUtils.getValidYearMonth("2024/04");
        }, "Should throw InvalidMonthYearExceptionHandler for invalid format.");
    }
    
	@Test
	void testGetValidYearMonth_InvalidMonth_ThrowsException() {
		assertThrows(InvalidMonthYearExceptionHandler.class, () -> {
			DateUtils.getValidYearMonth("2024-13");
		}, "Should throw InvalidMonthYearExceptionHandler for invalid month.");
	}

    @Test
    void testParseYearMonth_ValidInput_ReturnsParsedYearMonth() {
        YearMonth expected = YearMonth.of(2025, 3);
        YearMonth actual = DateUtils.parseYearMonth("2025-03");
        assertEquals(expected, actual, "Should correctly parse the valid YearMonth string");
    }
    
	@Test
	void testParseYearMonth_InvalidFormat_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            DateUtils.parseYearMonth("11-2023");
        }, "Should throw IllegalArgumentException for invalid format");
	}

    @Test
    void testParseYearMonth_InvalidCharacters_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            DateUtils.parseYearMonth("abcd-ef");
        }, "Should throw IllegalArgumentException for non-numeric input");
    }
}
