package com.budgetmaster.json.deserialization;

import java.io.IOException;
import java.time.YearMonth;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * Deserializes a two-element array [year, month] into a YearMonth object.
 * Expects the array to contain exactly two numbers: year and month.
 */
public class YearMonthDeserializer extends JsonDeserializer<YearMonth> {
    
    @Override
    public YearMonth deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.currentToken() == null) {
            p.nextToken();
        }
        
        if (p.currentToken().isStructStart()) {
            p.nextToken();
        }
        
        int year = p.nextIntValue(-1);
        if (year == -1) {
            throw new IOException("Invalid year value in YearMonth array");
        }
        
        p.nextToken();
        int month = p.nextIntValue(-1);
        if (month == -1) {
            throw new IOException("Invalid month value in YearMonth array");
        }
        
        try {
            return YearMonth.of(year, month);
        } catch (Exception e) {
            throw new IOException("Invalid YearMonth values: year=" + year + ", month=" + month, e);
        }
    }

    @Override
    public Class<?> handledType() {
        return YearMonth.class;
    }
} 