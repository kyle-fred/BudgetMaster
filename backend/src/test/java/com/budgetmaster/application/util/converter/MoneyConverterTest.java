package com.budgetmaster.application.util.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.budgetmaster.application.model.Money;
import com.budgetmaster.testsupport.builder.model.MoneyBuilder;
import com.budgetmaster.testsupport.constants.domain.MoneyConstants;

@DisplayName("Money Converter Tests")
class MoneyConverterTest {

    private MoneyConverter converter;
    private Money defaultMoney;

    @BeforeEach
    void setUp() {
        converter = new MoneyConverter();
        defaultMoney = MoneyBuilder.defaultIncome().build();
    }

    @Nested
    @DisplayName("Convert To Database Column Operations")
    class ConvertToDatabaseColumnOperations {
        
        @Test
        @DisplayName("Should convert money to big decimal when money is not null")
        void convertToDatabaseColumn_withNotNullMoney_returnsBigDecimal() {
            BigDecimal result = converter.convertToDatabaseColumn(defaultMoney);

            assertThat(result).isEqualTo(MoneyConstants.IncomeDefaults.AMOUNT);
        }

        @Test
        @DisplayName("Should return null when money is null")
        void convertToDatabaseColumn_withNullMoney_returnsNull() {
            BigDecimal result = converter.convertToDatabaseColumn(null);

            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("Convert To Entity Attribute Operations")
    class ConvertToEntityAttributeOperations {
        
        @ParameterizedTest(name = "Should convert {0} to money {1}")
        @CsvSource({
            "123.45, 123.45",
            "0.00, 0.00",
            "-123.45, -123.45"
        })
        @DisplayName("Should convert big decimal to money when big decimal is not null")
        void convertToEntityAttribute_withNotNullBigDecimal_returnsMoney(String input, String expected) {
            BigDecimal dbData = new BigDecimal(input);

            Money result = converter.convertToEntityAttribute(dbData);

            assertThat(result).isEqualTo(Money.of(new BigDecimal(expected)));
        }

        @Test
        @DisplayName("Should return null when big decimal is null")
        void convertToEntityAttribute_withNullBigDecimal_returnsNull() {
            Money result = converter.convertToEntityAttribute(null);

            assertThat(result).isNull();
        }
    }
}
