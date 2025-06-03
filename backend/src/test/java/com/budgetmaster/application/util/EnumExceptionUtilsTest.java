package com.budgetmaster.application.util;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.budgetmaster.testsupport.constants.EnumConstants;
import com.budgetmaster.testsupport.constants.ErrorConstants;
import com.budgetmaster.testsupport.dummyclasses.enums.DummyEnum;

import static org.assertj.core.api.Assertions.assertThat;

public class EnumExceptionUtilsTest {

    static class DummyClass {
        public DummyEnum color;
    }

    @Test
    void testExtractInvalidEnumValue_ValidExceptionMessage_ReturnsEnumConstant() {
        String message = ErrorConstants.Enum.FULL_EXCEPTION_INVALID_ENUM;
        String extracted = EnumExceptionUtils.extractInvalidEnumValue(message);

        assertThat(extracted).isEqualTo(ErrorConstants.Enum.ENUM_CONSTANT);
    }

    @Test
    void testExtractInvalidEnumValue_NoEnumPrefix_ReturnsNull() {
        String message = ErrorConstants.Enum.FULL_EXCEPTION_OTHER;
        String extracted = EnumExceptionUtils.extractInvalidEnumValue(message);

        assertThat(extracted).isNull();
    }

    @Test
    void testExtractEnumPart_ValidMessage_ReturnsEnumPart() {
        String message = ErrorConstants.Enum.NO_ENUM_PREFIX;
        String extracted = EnumExceptionUtils.extractEnumPart(message, 17);

        assertThat(extracted).isEqualTo(ErrorConstants.Enum.ENUM_PART);
    }

    @Test
    void testExtractEnumConstant_ValidEnumPart_ReturnsConstant() {
        String enumPart = ErrorConstants.Enum.ENUM_PART;
        String extracted = EnumExceptionUtils.extractEnumConstant(enumPart);

        assertThat(extracted).isEqualTo(ErrorConstants.Enum.ENUM_CONSTANT);
    }

    @Test
    void testExtractEnumConstant_NoDot_ReturnsSameString() {
        String enumPart = ErrorConstants.Enum.ENUM_CONSTANT;
        String extracted = EnumExceptionUtils.extractEnumConstant(enumPart);

        assertThat(extracted).isEqualTo(ErrorConstants.Enum.ENUM_CONSTANT);
    }

    @Test
    void testGetEnumValuesAsString_MultipleEnumValues_ReturnsCommaSeparatedString() {
        String values = EnumExceptionUtils.getEnumValuesAsString(DummyEnum.class);

        assertThat(values).isEqualTo(EnumConstants.ListOf.ALL_ENUM_VALUES);
    }

    // -- Find Enum Type Tests --
    
    @Test
    void testFindEnumType_ValidField_ReturnsEnumClass() {
        Optional<Class<? extends Enum<?>>> enumType = EnumExceptionUtils.findEnumType(DummyClass.class, EnumConstants.Invalid.ENUM_FIELD);

        assertThat(enumType).isPresent();
        assertThat(enumType.get()).isEqualTo(DummyEnum.class);
    }
    
    @Test
    void testFindEnumType_InvalidField_ReturnsEmptyOptional() {
        Optional<Class<? extends Enum<?>>> enumType = EnumExceptionUtils.findEnumType(DummyClass.class, EnumConstants.Invalid.FIELD_NAME);

        assertThat(enumType).isEmpty();
    }
}
