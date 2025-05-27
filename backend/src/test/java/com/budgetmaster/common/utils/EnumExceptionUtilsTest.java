package com.budgetmaster.common.utils;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.budgetmaster.testsupport.constants.Enums;
import com.budgetmaster.testsupport.constants.Messages;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EnumExceptionUtilsTest {
    // -- Enum for Testing --
    enum SampleEnum { 
    	RED, 
    	GREEN, 
    	BLUE 
    }

    // -- Model Class for Testing --
    static class ModelClass {
        public SampleEnum color;
    }

    // -- Extract Invalid Enum Value Tests --

    @Test
    void testExtractInvalidEnumValue_ValidExceptionMessage_ReturnsEnumConstant() {
        String message = Messages.EnumErrorMessageConstants.FULL_EXCEPTION_INVALID_ENUM;
        String extracted = EnumExceptionUtils.extractInvalidEnumValue(message);
        assertEquals(Messages.EnumErrorMessageConstants.ENUM_CONSTANT, extracted);
    }

    @Test
    void testExtractInvalidEnumValue_NoEnumPrefix_ReturnsNull() {
        String message = Messages.EnumErrorMessageConstants.FULL_EXCEPTION_OTHER;
        String extracted = EnumExceptionUtils.extractInvalidEnumValue(message);
        assertNull(extracted);
    }

    // -- Extract Enum Part Tests --

    @Test
    void testExtractEnumPart_ValidMessage_ReturnsEnumPart() {
        String message = Messages.EnumErrorMessageConstants.NO_ENUM_PREFIX;
        String extracted = EnumExceptionUtils.extractEnumPart(message, 17);
        assertEquals(Messages.EnumErrorMessageConstants.ENUM_PART, extracted);
    }

    // -- Extract Enum Constant Tests --

    @Test
    void testExtractEnumConstant_ValidEnumPart_ReturnsConstant() {
        String enumPart = Messages.EnumErrorMessageConstants.ENUM_PART;
        String extracted = EnumExceptionUtils.extractEnumConstant(enumPart);
        assertEquals(Messages.EnumErrorMessageConstants.ENUM_CONSTANT, extracted);
    }

    @Test
    void testExtractEnumConstant_NoDot_ReturnsSameString() {
        String enumPart = Messages.EnumErrorMessageConstants.ENUM_CONSTANT;
        String extracted = EnumExceptionUtils.extractEnumConstant(enumPart);
        assertEquals(Messages.EnumErrorMessageConstants.ENUM_CONSTANT, extracted);
    }

    // -- Get Enum Values as String Tests --

    @Test
    void testGetEnumValuesAsString_MultipleEnumValues_ReturnsCommaSeparatedString() {
        String values = EnumExceptionUtils.getEnumValuesAsString(SampleEnum.class);
        assertEquals(Messages.EnumErrorMessageConstants.LIST_OF_ENUM_VALUES, values);
    }

    // -- Find Enum Type Tests --
    
    @Test
    void testFindEnumType_ValidField_ReturnsEnumClass() {
        Optional<Class<? extends Enum<?>>> enumType = EnumExceptionUtils.findEnumType(ModelClass.class, Enums.ERROR_MESSAGE_INVALID_ENUM_FIELD);
        assertTrue(enumType.isPresent());
        assertEquals(SampleEnum.class, enumType.get());
    }
    
    @Test
    void testFindEnumType_InvalidField_ReturnsEmptyOptional() {
        Optional<Class<? extends Enum<?>>> enumType = EnumExceptionUtils.findEnumType(ModelClass.class, Enums.ERROR_MESSAGE_INVALID_FIELD_NAME);
        assertTrue(enumType.isEmpty());
    }
}
