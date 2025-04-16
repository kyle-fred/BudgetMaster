package com.budgetmaster.utils.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

class EnumExceptionUtilsTest {
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
        String message = "java.lang.IllegalArgumentException: No enum constant com.example.Color.BLUEE";
        String extracted = EnumExceptionUtils.extractInvalidEnumValue(message);
        assertEquals("BLUEE", extracted, "Should correctly extract invalid enum constant");
    }

    @Test
    void testExtractInvalidEnumValue_NoEnumPrefix_ReturnsNull() {
        String message = "java.lang.IllegalArgumentException: Some other error";
        String extracted = EnumExceptionUtils.extractInvalidEnumValue(message);
        assertNull(extracted, "Should return null when 'No enum constant' is not found");
    }

    // -- Extract Enum Part Tests --

    @Test
    void testExtractEnumPart_ValidMessage_ReturnsEnumPart() {
        String message = "No enum constant com.example.Color.BLUEE";
        String extracted = EnumExceptionUtils.extractEnumPart(message, 17);
        assertEquals("com.example.Color.BLUEE", extracted, "Should extract full enum reference");
    }

    // -- Extract Enum Constant Tests --

    @Test
    void testExtractEnumConstant_ValidEnumPart_ReturnsConstant() {
        String enumPart = "com.example.Color.BLUEE";
        String extracted = EnumExceptionUtils.extractEnumConstant(enumPart);
        assertEquals("BLUEE", extracted, "Should extract just the enum constant name");
    }

    @Test
    void testExtractEnumConstant_NoDot_ReturnsSameString() {
        String enumPart = "BLUEE";
        String extracted = EnumExceptionUtils.extractEnumConstant(enumPart);
        assertEquals("BLUEE", extracted, "Should return the same string if no dot exists");
    }

    // -- Create Error Response Tests --
    
    @Test
    void testCreateErrorResponse_ValidInputs_ReturnsCorrectErrorMap() {
        Map<String, Object> errorResponse = EnumExceptionUtils.createErrorResonse("YELLOW", "color", SampleEnum.class);
        
        assertNotNull(errorResponse, "Error response should not be null");
        assertTrue(errorResponse.containsKey("color"), "Response should contain field name as key");
        assertEquals("Invalid value 'YELLOW' for 'color'. Allowed values: [RED, GREEN, BLUE]",
                     errorResponse.get("color"), "Error message should list allowed values");
    }

    // -- Create Fallback Response Tests --
    
    @Test
    void testCreateFallbackResponse_ReturnsDefaultError() {
        Map<String, Object> fallbackResponse = EnumExceptionUtils.createFallbackResponse();
        assertNotNull(fallbackResponse, "Fallback response should not be null");
        assertTrue(fallbackResponse.containsKey("error"), "Response should contain \"error\" as key");
        assertEquals("Invalid enum value.", fallbackResponse.get("error"), "Should return default error message");
    }

    // -- Find Enum Type Tests --
    
    @Test
    void testFindEnumType_ValidField_ReturnsEnumClass() {
        Optional<Class<? extends Enum<?>>> enumType = EnumExceptionUtils.findEnumType(ModelClass.class, "color");
        assertTrue(enumType.isPresent(), "Should return an enum type");
        assertEquals(SampleEnum.class, enumType.get(), "Should correctly identify the enum class");
    }
    
    @Test
    void testFindEnumType_InvalidField_ReturnsEmptyOptional() {
        Optional<Class<? extends Enum<?>>> enumType = EnumExceptionUtils.findEnumType(ModelClass.class, "invalidField");
        assertTrue(enumType.isEmpty(), "Should return an empty Optional for nonexistent field");
    }
}
