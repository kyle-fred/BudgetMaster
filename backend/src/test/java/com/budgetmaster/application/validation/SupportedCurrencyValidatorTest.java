package com.budgetmaster.application.validation;

import com.budgetmaster.application.dto.MoneyRequest;
import com.budgetmaster.testsupport.builder.dto.MoneyRequestBuilder;
import com.budgetmaster.testsupport.constants.domain.MoneyConstants;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Supported Currency Validator Tests")
class SupportedCurrencyValidatorTest {

    private SupportedCurrencyValidator currencyValidator;

    @Mock
    private ConstraintValidatorContext context;

    private MoneyRequest request;

    @BeforeEach
    void setUp() {
        currencyValidator = new SupportedCurrencyValidator();
    }

    @Nested
    @DisplayName("Is Valid Operations")
    class IsValidOperations {
        
        @Test
        @DisplayName("Should return true when value is null")
        void isValid_withNullValue_returnsTrue() {
            assertTrue(currencyValidator.isValid(null, context));
        }

        @Test
        @DisplayName("Should return true when currency is null")
        void isValid_withNullCurrency_returnsTrue() {
            request = MoneyRequestBuilder.defaultZero()
                .withCurrency(null)
                .buildRequest();
            
            assertTrue(currencyValidator.isValid(request, context));
        }

        @Test
        @DisplayName("Should return true when currency is supported")
        void isValid_withSupportedCurrency_returnsTrue() {
            request = MoneyRequestBuilder.defaultZero()
                .buildRequest();
            
            assertTrue(currencyValidator.isValid(request, context));
        }

        @Test
        @DisplayName("Should return false when currency is not supported")
        void isValid_withUnsupportedCurrency_returnsFalse() {
            request = MoneyRequestBuilder.defaultZero()
                .withCurrency(MoneyConstants.InvalidValues.EUR)
                .buildRequest();
            
            assertFalse(currencyValidator.isValid(request, context));
        }
    }
} 