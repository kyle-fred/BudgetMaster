package com.budgetmaster.utils.exception;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.budgetmaster.constants.common.StringConstants;
import com.budgetmaster.constants.error.ErrorMessages.CommonErrorMessages;
import com.budgetmaster.constants.error.ErrorMessages.EnumErrorMessages;

public class EnumExceptionUtils {
    
    /**
     * Extracts the invalid enum constant from the exception message.
     */
    public static String extractInvalidEnumValue(String exceptionMessage) {
        int startIndex = exceptionMessage.indexOf(StringConstants.EXCEPTION_PREFIX_NO_ENUM_CONSTANT);
        
        if (startIndex == -1) {
            return null;
        }
        
        startIndex += StringConstants.EXCEPTION_PREFIX_NO_ENUM_CONSTANT.length();
        String enumPart = extractEnumPart(exceptionMessage, startIndex);
        return extractEnumConstant(enumPart);
    }
    
    /**
     * Extracts the portion of the message representing the invalid enum value.
     */
    public static String extractEnumPart(String message, int startIndex) {
        int endIndex = message.indexOf(StringConstants.STRING_CONSTANTS_SPACE, startIndex);
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
        enumPart = enumPart.replace(StringConstants.STRING_CONSTANTS_NEW_LINE, StringConstants.STRING_CONSTANTS_EMPTY);
        int lastDotIndex = enumPart.lastIndexOf(StringConstants.STRING_CONSTANTS_DOT);
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
                EnumErrorMessages.ERROR_MESSAGE_INVALID_ENUM_VALUE_FORMAT,
                invalidValue, 
                fieldName, 
                Arrays.stream(enumType.getEnumConstants())
                    .map(Enum::name)
                    .collect(Collectors.joining(StringConstants.STRING_CONSTANTS_COMMA_SPACE))
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
    	fallbackResponse.put(CommonErrorMessages.ERROR_MESSAGE_ERROR, EnumErrorMessages.ERROR_MESSAGE_INVALID_ENUM_VALUE);
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