package com.budgetmaster.utils.exception;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class EnumExceptionUtils {
    
    /**
     * Extracts the invalid enum constant from the exception message.
     */
    public static String extractInvalidEnumValue(String exceptionMessage) {
        String prefix = "No enum constant ";
        int startIndex = exceptionMessage.indexOf(prefix);
        
        if (startIndex == -1) {
            return null;
        }
        
        startIndex += prefix.length();
        String enumPart = extractEnumPart(exceptionMessage, startIndex);
        return extractEnumConstant(enumPart);
    }
    
    /**
     * Extracts the portion of the message representing the invalid enum value.
     */
    public static String extractEnumPart(String message, int startIndex) {
        int endIndex = message.indexOf(' ', startIndex);
        if (endIndex == -1) {
            return message.substring(startIndex);
        } else {
            return message.substring(startIndex, endIndex);
        }
    }
    
    /**
     * Extracts the enum constant name from its fully qualified reference.
     */
    public static String extractEnumConstant(String enumPart) {
        enumPart = enumPart.replace("\n", "");
        int lastDotIndex = enumPart.lastIndexOf('.');
        if (lastDotIndex != -1) {
            return enumPart.substring(lastDotIndex + 1);
        } else {
            return enumPart;
        }
    }
    
    /**
     * Creates an error response with allowed enum values.
     */
    public static Map<String, Object> createErrorResonse(String invalidValue, String fieldName, Class<? extends Enum<?>> enumType) {
    	String errorMessage =  String.format(
                "Invalid value '%s' for '%s'. Allowed values: [%s]",
                invalidValue, 
                fieldName, 
                Arrays.stream(enumType.getEnumConstants())
                    .map(Enum::name)
                    .collect(Collectors.joining(", "))
            );
    	
    	Map<String, Object> errorResponse = new HashMap<>();
    	errorResponse.put(fieldName, errorMessage);
        
        return errorResponse;
    }
    
    /**
     * Creates a fallback error response when the enum type is not found.
     */
    public static Map<String, Object> createFallbackResponse() {
    	Map<String, Object> fallbackResponse = new HashMap<>();
    	fallbackResponse.put("error", "Invalid enum value.");
    	return fallbackResponse;
    }
    
    /**
     * Finds the enum type for a given field in a model class.
     */
    public static Optional<Class<? extends Enum<?>>> findEnumType(Class<?> modelClass, String fieldName) {
    	try {
            @SuppressWarnings("unchecked")
            Class<? extends Enum<?>> enumType = (Class<? extends Enum<?>>) modelClass.getDeclaredField(fieldName).getType();
            return Optional.ofNullable(enumType);
    	} catch (NoSuchFieldException e) {
    		return Optional.empty();
    	}
    }
}