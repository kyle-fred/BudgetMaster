package com.budgetmaster.constants.validation;

public class ValidationPatterns {

    public static class Date {
        private Date() {}
        
        public static final String YEAR_MONTH_REGEX = "^\\d{4}-(?:0[1-9]|1[0-2])$";
    }
    
}

