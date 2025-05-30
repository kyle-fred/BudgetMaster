package com.budgetmaster.application.util.converter;

import java.math.BigDecimal;

import com.budgetmaster.application.model.Money;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
class MoneyConverter implements AttributeConverter<Money, BigDecimal> {
    @Override
    public BigDecimal convertToDatabaseColumn(Money money) {
        if (money != null) {
            return money.getAmount();
        }
        return null;
    }

    @Override
    public Money convertToEntityAttribute(BigDecimal dbData) {
        if (dbData != null) {
            return Money.of(dbData);
        }
        return null;
    }
}