package com.budgetmaster.application.util;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.budgetmaster.testsupport.constants.EnumConstants;
import com.budgetmaster.testsupport.constants.ErrorConstants;
import com.budgetmaster.testsupport.dummyclasses.enums.DummyEnum;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Enum Exception Utils Tests")
class EnumExceptionUtilsTest {

    static class DummyClass {
        public DummyEnum color;
    }

    @Nested
    @DisplayName("Extract Invalid Enum Value Operations")
    class ExtractInvalidEnumValueOperations {
        
        @Test
        @DisplayName("Should extract enum constant from valid exception message")
        void extractInvalidEnumValue_withValidExceptionMessage_returnsEnumConstant() {
            String message = ErrorConstants.Enum.FULL_EXCEPTION_INVALID_ENUM;
            String extracted = EnumExceptionUtils.extractInvalidEnumValue(message);

            assertThat(extracted).isEqualTo(ErrorConstants.Enum.ENUM_CONSTANT);
        }

        @Test
        @DisplayName("Should return null when message has no enum prefix")
        void extractInvalidEnumValue_withNoEnumPrefix_returnsNull() {
            String message = ErrorConstants.Enum.FULL_EXCEPTION_OTHER;
            String extracted = EnumExceptionUtils.extractInvalidEnumValue(message);

            assertThat(extracted).isNull();
        }
    }

    @Nested
    @DisplayName("Extract Enum Part Operations")
    class ExtractEnumPartOperations {
        
        @Test
        @DisplayName("Should extract enum part from valid message")
        void extractEnumPart_withValidMessage_returnsEnumPart() {
            String message = ErrorConstants.Enum.NO_ENUM_PREFIX;
            String extracted = EnumExceptionUtils.extractEnumPart(message, 17);

            assertThat(extracted).isEqualTo(ErrorConstants.Enum.ENUM_PART);
        }
    }

    @Nested
    @DisplayName("Extract Enum Constant Operations")
    class ExtractEnumConstantOperations {
        
        @Test
        @DisplayName("Should extract enum constant from valid enum part")
        void extractEnumConstant_withValidEnumPart_returnsConstant() {
            String enumPart = ErrorConstants.Enum.ENUM_PART;
            String extracted = EnumExceptionUtils.extractEnumConstant(enumPart);

            assertThat(extracted).isEqualTo(ErrorConstants.Enum.ENUM_CONSTANT);
        }

        @Test
        @DisplayName("Should return same string when no dot is present")
        void extractEnumConstant_withNoDot_returnsSameString() {
            String enumPart = ErrorConstants.Enum.ENUM_CONSTANT;
            String extracted = EnumExceptionUtils.extractEnumConstant(enumPart);

            assertThat(extracted).isEqualTo(ErrorConstants.Enum.ENUM_CONSTANT);
        }
    }

    @Nested
    @DisplayName("Get Enum Values Operations")
    class GetEnumValuesOperations {
        
        @Test
        @DisplayName("Should return comma-separated string of enum values")
        void getEnumValuesAsString_withMultipleEnumValues_returnsCommaSeparatedString() {
            String values = EnumExceptionUtils.getEnumValuesAsString(DummyEnum.class);

            assertThat(values).isEqualTo(EnumConstants.ListOf.ALL_ENUM_VALUES);
        }
    }

    @Nested
    @DisplayName("Find Enum Type Operations")
    class FindEnumTypeOperations {
        
        @Test
        @DisplayName("Should return enum class for valid field")
        void findEnumType_withValidField_returnsEnumClass() {
            Optional<Class<? extends Enum<?>>> enumType = EnumExceptionUtils.findEnumType(DummyClass.class, EnumConstants.Invalid.ENUM_FIELD);

            assertThat(enumType).isPresent();
            assertThat(enumType.get()).isEqualTo(DummyEnum.class);
        }
        
        @Test
        @DisplayName("Should return empty optional for invalid field")
        void findEnumType_withInvalidField_returnsEmptyOptional() {
            Optional<Class<? extends Enum<?>>> enumType = EnumExceptionUtils.findEnumType(DummyClass.class, EnumConstants.Invalid.FIELD_NAME);

            assertThat(enumType).isEmpty();
        }
    }
}
