package com.budgetmaster.common.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.budgetmaster.application.model.Money;
import com.budgetmaster.testsupport.money.constants.MoneyConstants;
import com.budgetmaster.testsupport.money.factory.MoneyFactory;

class MoneyConverterTest {
    // -- Test Objects --
    private MoneyConverter converter;

    private Money testMoney;

    @BeforeEach
    void setUp() {
        converter = new MoneyConverter();
        testMoney = MoneyFactory.createIncomeMoney();
    }

    @Test
    void convertToDatabaseColumn_WhenMoneyIsNotNull_ReturnsBigDecimal() {
        BigDecimal result = converter.convertToDatabaseColumn(testMoney);

        assertThat(result).isEqualTo(MoneyConstants.IncomeDefaults.AMOUNT);
    }

    @Test
    void convertToDatabaseColumn_WhenMoneyIsNull_ReturnsNull() {
        BigDecimal result = converter.convertToDatabaseColumn(null);

        assertThat(result).isNull();
    }

    @ParameterizedTest
    @CsvSource({
        "123.45, 123.45",
        "0.00, 0.00",
        "-123.45, -123.45"
    })
    void convertToEntityAttribute_WhenBigDecimalIsNotNull_ReturnsMoney(String input, String expected) {
        BigDecimal dbData = new BigDecimal(input);

        Money result = converter.convertToEntityAttribute(dbData);

        assertThat(result).isEqualTo(Money.of(new BigDecimal(expected)));
    }

    @Test
    void convertToEntityAttribute_WhenBigDecimalIsNull_ReturnsNull() {
        Money result = converter.convertToEntityAttribute(null);

        assertThat(result).isNull();
    }
}
