package com.budgetmaster.json.deserialization;

import java.io.IOException;
import java.math.BigDecimal;

import com.budgetmaster.constants.error.ErrorMessages;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * Deserializes string values back into BigDecimal objects.
 * Handles null values and invalid number formats gracefully.
 */
public class BigDecimalToStringDeserializer extends JsonDeserializer<BigDecimal> {
    
    @Override
    public BigDecimal deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = extractValue(p);
        if (isNullOrEmpty(value)) {
            return null;
        }
        return parseBigDecimal(value);
    }
    
    private String extractValue(JsonParser p) throws IOException {
        if (p.currentToken().isNumeric()) {
            return p.getDecimalValue().toPlainString();
        }
        return p.getText();
    }
    
    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
    
    private BigDecimal parseBigDecimal(String value) throws IOException {
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            throw new IOException(
                String.format(ErrorMessages.Serialization.INVALID_BIG_DECIMAL_VALUE, value),
                e
            );
        }
    }

    @Override
    public Class<?> handledType() {
        return BigDecimal.class;
    }
} 