package com.budgetmaster.json.serialization;

import java.io.IOException;
import java.math.BigDecimal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Serializes BigDecimal values to their string representation to preserve precision and scale.
 * This is particularly important for financial calculations where precision is critical.
 */
public class BigDecimalToStringSerializer extends JsonSerializer<BigDecimal> {
    
    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }
        gen.writeString(value.toPlainString());
    }

    @Override
    public Class<BigDecimal> handledType() {
        return BigDecimal.class;
    }
} 