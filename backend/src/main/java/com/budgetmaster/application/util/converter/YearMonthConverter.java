package com.budgetmaster.application.util.converter;

import java.time.YearMonth;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class YearMonthConverter implements AttributeConverter<YearMonth, String> {

  @Override
  public String convertToDatabaseColumn(YearMonth attribute) {
    if (attribute != null) {
      return attribute.toString();
    } else {
      return null;
    }
  }

  @Override
  public YearMonth convertToEntityAttribute(String dbData) {
    if (dbData != null) {
      return YearMonth.parse(dbData);
    } else {
      return null;
    }
  }
}
