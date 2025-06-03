package com.budgetmaster.application.validation;

import com.budgetmaster.application.dto.MoneyRequest;
import com.budgetmaster.testsupport.builder.dto.MoneyRequestBuilder;
import com.budgetmaster.testsupport.constants.domain.MoneyConstants;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SupportedCurrencyValidatorTest {

    private SupportedCurrencyValidator currencyValidator;

    @Mock
    private ConstraintValidatorContext context;

    private MoneyRequest request;

    @BeforeEach
    void setUp() {
        currencyValidator = new SupportedCurrencyValidator();
    }

    @Test
    void isValid_WhenValueIsNull_ReturnsTrue() {
        assertTrue(currencyValidator.isValid(null, context));
    }

    @Test
    void isValid_WhenCurrencyIsNull_ReturnsTrue() {
        request = MoneyRequestBuilder.defaultZero()
            .withCurrency(null)
            .buildRequest();
        
        assertTrue(currencyValidator.isValid(request, context));
    }

    @Test
    void isValid_WhenCurrencyIsSupported_ReturnsTrue() {
        request = MoneyRequestBuilder.defaultZero()
            .buildRequest();
        
        assertTrue(currencyValidator.isValid(request, context));
    }

    @Test
    void isValid_WhenCurrencyIsNotSupported_ReturnsFalse() {
        request = MoneyRequestBuilder.defaultZero()
            .withCurrency(MoneyConstants.InvalidValues.EUR)
            .buildRequest();
        
        assertFalse(currencyValidator.isValid(request, context));
    }
} 