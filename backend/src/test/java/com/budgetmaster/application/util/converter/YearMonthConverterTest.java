package com.budgetmaster.application.util.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.YearMonth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.budgetmaster.testsupport.constants.domain.IncomeConstants;

public class YearMonthConverterTest {
    // -- Test Objects --
    private YearMonthConverter converter;

    private YearMonth testYearMonth;

    @BeforeEach
    void setUp() {
        converter = new YearMonthConverter();
        testYearMonth = IncomeConstants.Default.YEAR_MONTH;
    }

    @Test
    void convertToDatabaseColumn_WhenYearMonthIsNotNull_ReturnsString() {
        String result = converter.convertToDatabaseColumn(testYearMonth);

        assertThat(result).isEqualTo(IncomeConstants.Default.YEAR_MONTH_STRING);
    }

    @Test
    void convertToDatabaseColumn_WhenYearMonthIsNull_ReturnsNull() {
        String result = converter.convertToDatabaseColumn(null);

        assertThat(result).isNull();
    }

    @ParameterizedTest
    @CsvSource({
        "2000-01, 2000-01",
        "1999-12, 1999-12",
        "2001-01, 2001-01"
    })
    void convertToEntityAttribute_WhenStringIsNotNull_ReturnsYearMonth(String input, String expected) {
        YearMonth result = converter.convertToEntityAttribute(input);

        assertThat(result).isEqualTo(YearMonth.parse(expected));
    }

    @Test
    void convertToEntityAttribute_WhenStringIsNull_ReturnsNull() {
        YearMonth result = converter.convertToEntityAttribute(null);

        assertThat(result).isNull();
    }
}
