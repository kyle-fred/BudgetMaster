package com.budgetmaster.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import com.budgetmaster.testsupport.builder.model.DummyConfigEntityBuilder;
import com.budgetmaster.testsupport.constants.domain.DummyConfigConstants;
import com.budgetmaster.testsupport.dummyclasses.entity.DummyConfigEntity;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@Import(JacksonConfig.class)
@DisplayName("Jackson Config Tests")
class JacksonConfigTest {

  @Autowired private ObjectMapper objectMapper;

  private DummyConfigEntity dummyConfigEntity;

  @BeforeEach
  void setUp() {
    dummyConfigEntity = DummyConfigEntityBuilder.defaultEntity().build();
  }

  @Nested
  @DisplayName("Property Name Formatting Operations")
  class PropertyNameFormattingOperations {

    @Test
    @DisplayName("Should use kebab case for property names")
    void serialize_withEntity_usesKebabCaseForPropertyNames() throws Exception {
      String json = objectMapper.writeValueAsString(dummyConfigEntity);

      assertThat(json)
          .contains(DummyConfigConstants.Json.KEBAB_CASE)
          .doesNotContain(DummyConfigConstants.Json.CAMEL_CASE);
    }
  }

  @Nested
  @DisplayName("Big Decimal Serialization Operations")
  class BigDecimalSerializationOperations {

    @Test
    @DisplayName("Should serialize big decimal as string")
    void serialize_withBigDecimal_serializesAsString() throws Exception {
      String json = objectMapper.writeValueAsString(dummyConfigEntity);

      assertThat(json)
          .contains(DummyConfigConstants.Json.BIG_DECIMAL_AS_STRING)
          .doesNotContain(DummyConfigConstants.Json.BIG_DECIMAL_AS_NUMBER);
    }

    @Test
    @DisplayName("Should deserialize string to big decimal")
    void deserialize_withBigDecimalString_returnsBigDecimal() throws Exception {
      String json = DummyConfigConstants.Json.BIG_DECIMAL_AS_STRING_JSON;

      DummyConfigEntity dummyConfigEntity = objectMapper.readValue(json, DummyConfigEntity.class);

      assertThat(dummyConfigEntity.getBigDecimal())
          .isEqualTo(new BigDecimal(DummyConfigConstants.Default.BIG_DECIMAL));
    }
  }

  @Nested
  @DisplayName("Local Date Time Serialization Operations")
  class LocalDateTimeSerializationOperations {

    @Test
    @DisplayName("Should serialize local date time in standard format")
    void serialize_withLocalDateTime_serializesInStandardFormat() throws Exception {
      String json = objectMapper.writeValueAsString(dummyConfigEntity);

      assertThat(json).contains(DummyConfigConstants.Json.TIME_AS_FORMATTED_STRING);
    }

    @Test
    @DisplayName("Should deserialize string to local date time")
    void deserialize_withLocalDateTimeString_returnsLocalDateTime() throws Exception {
      String json = DummyConfigConstants.Json.TIME_AS_STRING;

      LocalDateTime dateTime = objectMapper.readValue(json, LocalDateTime.class);

      assertThat(dateTime).isEqualTo(dummyConfigEntity.getTime());
    }
  }
}
