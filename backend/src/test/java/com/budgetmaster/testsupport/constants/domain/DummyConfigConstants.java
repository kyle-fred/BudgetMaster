package com.budgetmaster.testsupport.constants.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DummyConfigConstants {
    private DummyConfigConstants() {}

    public static final class Default {
        private Default() {}

        public static final String MULTI_WORD_PROPERTY = "multi-word-property";
        public static final String BIG_DECIMAL = "100.00";
        public static final LocalDateTime TIME = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
        public static final String TIME_STRING = TIME.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static final class Json {
        private Json() {}

        public static final String KEBAB_CASE = "\"multi-word-property\":";
        public static final String CAMEL_CASE = "\"multiWordProperty\":";

        public static final String BIG_DECIMAL_AS_STRING = "\"big-decimal\":\"100.00\"";
        public static final String BIG_DECIMAL_AS_NUMBER = "\"big-decimal\":100.00";

        public static final String BIG_DECIMAL_AS_STRING_JSON = "{\"big-decimal\":\"100.00\"}";

        public static final String TIME_AS_FORMATTED_STRING = "\"time\":\"2000-01-01 00:00:00\"";
        public static final String TIME_AS_STRING = "\"2000-01-01 00:00:00\"";
    }
}