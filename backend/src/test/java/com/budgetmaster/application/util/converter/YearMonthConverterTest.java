package com.budgetmaster.application.util.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.YearMonth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.budgetmaster.testsupport.constants.domain.IncomeConstants;

@DisplayName("Year Month Converter Tests")
class YearMonthConverterTest {

    private YearMonthConverter converter;
    private YearMonth testYearMonth;

    @BeforeEach
    void setUp() {
        converter = new YearMonthConverter();
        testYearMonth = IncomeConstants.Default.YEAR_MONTH;
    }

    @Nested
    @DisplayName("Convert To Database Column Operations")
    class ConvertToDatabaseColumnOperations {
        
        @Test
        @DisplayName("Should convert year month to string when year month is not null")
        void convertToDatabaseColumn_withNotNullYearMonth_returnsString() {
            String result = converter.convertToDatabaseColumn(testYearMonth);

            assertThat(result).isEqualTo(IncomeConstants.Default.YEAR_MONTH_STRING);
        }

        @Test
        @DisplayName("Should return null when year month is null")
        void convertToDatabaseColumn_withNullYearMonth_returnsNull() {
            String result = converter.convertToDatabaseColumn(null);

            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("Convert To Entity Attribute Operations")
    class ConvertToEntityAttributeOperations {
        
        @ParameterizedTest(name = "Should convert {0} to year month {1}")
        @CsvSource({
            "2000-01, 2000-01",
            "1999-12, 1999-12",
            "2001-01, 2001-01"
        })
        @DisplayName("Should convert string to year month when string is not null")
        void convertToEntityAttribute_withNotNullString_returnsYearMonth(String input, String expected) {
            YearMonth result = converter.convertToEntityAttribute(input);

            assertThat(result).isEqualTo(YearMonth.parse(expected));
        }

        @Test
        @DisplayName("Should return null when string is null")
        void convertToEntityAttribute_withNullString_returnsNull() {
            YearMonth result = converter.convertToEntityAttribute(null);

            assertThat(result).isNull();
        }
    }
}
