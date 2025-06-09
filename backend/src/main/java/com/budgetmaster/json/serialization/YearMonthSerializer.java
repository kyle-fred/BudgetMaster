package com.budgetmaster.json.serialization;

import java.io.IOException;
import java.time.YearMonth;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Serializes YearMonth values into a two-element array containing [year, month]. This format is
 * more compact and easier to work with than string representations.
 */
public class YearMonthSerializer extends JsonSerializer<YearMonth> {

  @Override
  public void serialize(YearMonth value, JsonGenerator gen, SerializerProvider serializers)
      throws IOException {
    if (value == null) {
      gen.writeNull();
      return;
    }

    gen.writeStartArray();
    gen.writeNumber(value.getYear());
    gen.writeNumber(value.getMonthValue());
    gen.writeEndArray();
  }

  @Override
  public Class<YearMonth> handledType() {
    return YearMonth.class;
  }
}
