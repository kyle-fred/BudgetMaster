package com.budgetmaster.json.deserialization;

import java.io.IOException;
import java.math.BigDecimal;

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
        String value = p.getText();
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            throw new IOException("Invalid BigDecimal value: " + value, e);
        }
    }

    @Override
    public Class<?> handledType() {
        return BigDecimal.class;
    }
} 