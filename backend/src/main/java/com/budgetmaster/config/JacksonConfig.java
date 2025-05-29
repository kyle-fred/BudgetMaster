package com.budgetmaster.config;

import java.time.format.DateTimeFormatter;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import com.budgetmaster.json.serialization.BigDecimalToStringSerializer;
import com.budgetmaster.constants.date.DateFormats;
import com.budgetmaster.json.deserialization.BigDecimalToStringDeserializer;
import com.budgetmaster.json.serialization.YearMonthSerializer;

@Configuration
public class JacksonConfig {

    public static final DateTimeFormatter STANDARD_DATE_TIME_FORMATTER =
        DateTimeFormatter.ofPattern(DateFormats.STANDARD_DATE_TIME);

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> builder
            // Global serialization settings
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .propertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE)
            
            // Custom serializations
            .serializers(
                new LocalDateTimeSerializer(STANDARD_DATE_TIME_FORMATTER),
                new BigDecimalToStringSerializer(),
                new YearMonthSerializer()
            )
            .deserializers(
                new LocalDateTimeDeserializer(STANDARD_DATE_TIME_FORMATTER),
                new BigDecimalToStringDeserializer()
            );
    }
}
