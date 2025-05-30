package com.budgetmaster.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.budgetmaster.testsupport.builder.DummyConfigEntityBuilder;
import com.budgetmaster.testsupport.constants.domain.DummyConfigConstants;
import com.budgetmaster.testsupport.mockentity.DummyConfigEntity;

@SpringBootTest
@Import(JacksonConfig.class)
class JacksonConfigTest {

    // -- Dependencies --
    @Autowired
    private ObjectMapper objectMapper;

    // -- Test Objects --
    private DummyConfigEntity dummyConfigEntity;

    @BeforeEach
    void setUp() {
        dummyConfigEntity = DummyConfigEntityBuilder.defaultEntity().build();
    }

    @Test
    void shouldUseKebabCaseForPropertyNames() throws Exception {
        String json = objectMapper.writeValueAsString(dummyConfigEntity);
        
        assertThat(json)
            .contains(DummyConfigConstants.Json.KEBAB_CASE)
            .doesNotContain(DummyConfigConstants.Json.CAMEL_CASE);
    }

    @Test
    void shouldSerializeBigDecimalAsString() throws Exception {
        String json = objectMapper.writeValueAsString(dummyConfigEntity);

        assertThat(json)
            .contains(DummyConfigConstants.Json.BIG_DECIMAL_AS_STRING)
            .doesNotContain(DummyConfigConstants.Json.BIG_DECIMAL_AS_NUMBER);
    }

    @Test
    void shouldDeserializeStringToBigDecimal() throws Exception {
        String json = DummyConfigConstants.Json.BIG_DECIMAL_AS_STRING_JSON;

        DummyConfigEntity dummyConfigEntity = objectMapper.readValue(json, DummyConfigEntity.class);

        assertThat(dummyConfigEntity.getBigDecimal())
            .isEqualTo(new BigDecimal(DummyConfigConstants.Default.BIG_DECIMAL));
    }

    @Test
    void shouldSerializeLocalDateTimeInStandardFormat() throws Exception {
        String json = objectMapper.writeValueAsString(dummyConfigEntity);

        assertThat(json)
            .contains(DummyConfigConstants.Json.TIME_AS_FORMATTED_STRING);
    }

    @Test
    void shouldDeserializeStringToLocalDateTime() throws Exception {
        String json = DummyConfigConstants.Json.TIME_AS_STRING;

        LocalDateTime dateTime = objectMapper.readValue(json, LocalDateTime.class);

        assertThat(dateTime)
            .isEqualTo(dummyConfigEntity.getTime());
    }
} 