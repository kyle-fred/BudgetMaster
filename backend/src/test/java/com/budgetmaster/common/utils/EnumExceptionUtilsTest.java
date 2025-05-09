package com.budgetmaster.common.utils;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.budgetmaster.test.constants.TestCommonData;
import com.budgetmaster.test.constants.TestMessages;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        String message = TestMessages.EnumErrorMessageConstants.ERROR_MESSAGE_FULL_EXCEPTION_INVALID_ENUM;
        String extracted = EnumExceptionUtils.extractInvalidEnumValue(message);
        assertEquals(TestMessages.EnumErrorMessageConstants.ERROR_MESSAGE_ENUM_CONSTANT, extracted);
    }

    @Test
    void testExtractInvalidEnumValue_NoEnumPrefix_ReturnsNull() {
        String message = TestMessages.EnumErrorMessageConstants.ERROR_MESSAGE_FULL_EXCEPTION_OTHER;
        String extracted = EnumExceptionUtils.extractInvalidEnumValue(message);
        assertNull(extracted);
    }

    // -- Extract Enum Part Tests --

    @Test
    void testExtractEnumPart_ValidMessage_ReturnsEnumPart() {
        String message = TestMessages.EnumErrorMessageConstants.ERROR_MESSAGE_NO_ENUM_PREFIX;
        String extracted = EnumExceptionUtils.extractEnumPart(message, 17);
        assertEquals(TestMessages.EnumErrorMessageConstants.ERROR_MESSAGE_ENUM_PART, extracted);
    }

    // -- Extract Enum Constant Tests --

    @Test
    void testExtractEnumConstant_ValidEnumPart_ReturnsConstant() {
        String enumPart = TestMessages.EnumErrorMessageConstants.ERROR_MESSAGE_ENUM_PART;
        String extracted = EnumExceptionUtils.extractEnumConstant(enumPart);
        assertEquals(TestMessages.EnumErrorMessageConstants.ERROR_MESSAGE_ENUM_CONSTANT, extracted);
    }

    @Test
    void testExtractEnumConstant_NoDot_ReturnsSameString() {
        String enumPart = TestMessages.EnumErrorMessageConstants.ERROR_MESSAGE_ENUM_CONSTANT;
        String extracted = EnumExceptionUtils.extractEnumConstant(enumPart);
        assertEquals(TestMessages.EnumErrorMessageConstants.ERROR_MESSAGE_ENUM_CONSTANT, extracted);
    }

    // -- Create Error Response Tests --
    
    @Test
    void testCreateErrorResponse_ValidInputs_ReturnsCorrectErrorMap() {
        Map<String, Object> errorResponse = EnumExceptionUtils.createErrorResonse(  TestCommonData.EnumExceptionUtilsTestDataConstants.ERROR_MESSAGE_INVALID_ENUM_VALUE, 
                                                                                    TestCommonData.EnumExceptionUtilsTestDataConstants.ERROR_MESSAGE_INVALID_ENUM_FIELD, 
                                                                                    SampleEnum.class);
        
        assertNotNull(errorResponse);
        assertTrue(errorResponse.containsKey(TestCommonData.EnumExceptionUtilsTestDataConstants.ERROR_MESSAGE_INVALID_ENUM_FIELD));
        assertEquals(TestMessages.EnumErrorMessageConstants.ERROR_MESSAGE_INVALID_ENUM_VALUE_RESPONSE,
                     errorResponse.get(TestCommonData.EnumExceptionUtilsTestDataConstants.ERROR_MESSAGE_INVALID_ENUM_FIELD));
    }

    // -- Create Fallback Response Tests --
    
    @Test
    void testCreateFallbackResponse_ReturnsDefaultError() {
        Map<String, Object> fallbackResponse = EnumExceptionUtils.createFallbackResponse();
        assertNotNull(fallbackResponse);
        assertTrue(fallbackResponse.containsKey(TestMessages.CommonErrorMessageConstants.ERROR));
        assertEquals(TestMessages.EnumErrorMessageConstants.ERROR_MESSAGE_FALLBACK_MESSAGE, fallbackResponse.get(TestMessages.CommonErrorMessageConstants.ERROR));
    }

    // -- Find Enum Type Tests --
    
    @Test
    void testFindEnumType_ValidField_ReturnsEnumClass() {
        Optional<Class<? extends Enum<?>>> enumType = EnumExceptionUtils.findEnumType(ModelClass.class, TestCommonData.EnumExceptionUtilsTestDataConstants.ERROR_MESSAGE_INVALID_ENUM_FIELD);
        assertTrue(enumType.isPresent());
        assertEquals(SampleEnum.class, enumType.get());
    }
    
    @Test
    void testFindEnumType_InvalidField_ReturnsEmptyOptional() {
        Optional<Class<? extends Enum<?>>> enumType = EnumExceptionUtils.findEnumType(ModelClass.class, TestCommonData.EnumExceptionUtilsTestDataConstants.ERROR_MESSAGE_INVALID_FIELD_NAME);
        assertTrue(enumType.isEmpty());
    }
}
