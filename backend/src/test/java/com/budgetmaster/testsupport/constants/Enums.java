package com.budgetmaster.testsupport.constants;

public final class Enums {
    private Enums() {}

    public static class Invalid {
        private Invalid() {}

        public static final String ENUM_VALUE = "YELLOW";
        public static final String ENUM_FIELD = "color";
        public static final String FIELD_NAME = "invalidField";
    }

    public static class ListOf {
        private ListOf() {}

        public static final String ALL_ENUM_VALUES = "RED, GREEN, BLUE";
    }
}
