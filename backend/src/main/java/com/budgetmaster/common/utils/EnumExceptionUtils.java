package com.budgetmaster.common.utils;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import com.budgetmaster.common.constants.string.StringConstants;

public class EnumExceptionUtils {
    
    /**
     * Extracts the invalid enum constant from the exception message.
     */
    public static String extractInvalidEnumValue(String exceptionMessage) {
        int startIndex = exceptionMessage.indexOf(StringConstants.ExceptionMessages.NO_ENUM_CONSTANT_PREFIX);
        
        if (startIndex == -1) {
            return null;
        }
        
        startIndex += StringConstants.ExceptionMessages.NO_ENUM_CONSTANT_PREFIX.length();
        String enumPart = extractEnumPart(exceptionMessage, startIndex);
        return extractEnumConstant(enumPart);
    }
    
    /**
     * Extracts the portion of the message representing the invalid enum value.
     */
    public static String extractEnumPart(String message, int startIndex) {
        int endIndex = message.indexOf(StringConstants.Punctuation.SPACE, startIndex);
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
        enumPart = enumPart.replace(StringConstants.Punctuation.NEW_LINE, StringConstants.Punctuation.EMPTY);
        int lastDotIndex = enumPart.lastIndexOf(StringConstants.Punctuation.DOT);
        if (lastDotIndex != -1) {
            return enumPart.substring(lastDotIndex + 1);
        } else {
            return enumPart;
        }
    }
    
    /**
     * Gets all enum values as a comma-separated string.
     */
    public static String getEnumValuesAsString(Class<? extends Enum<?>> enumType) {
        return Arrays.stream(enumType.getEnumConstants())
            .map(Enum::name)
            .collect(Collectors.joining(StringConstants.Punctuation.COMMA_SPACE));
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