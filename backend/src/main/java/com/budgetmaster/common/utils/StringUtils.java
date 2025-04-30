package com.budgetmaster.common.utils;

public class StringUtils {
    
    /**
     * Capitalizes a string by converting it to uppercase.
     * Returns null if the input is null.
     * 
     * @param str The string to capitalize
     */
    public static String capitalize(String str) {
        if (str == null) {
            return null;
        }
        
        return str.toUpperCase();
    }
} 