package com.budgetmaster.testsupport.builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.budgetmaster.testsupport.config.DummyConfigEntity;
import com.budgetmaster.testsupport.constants.domain.DummyConfigConstants;

public class DummyConfigEntityBuilder {
    
    private String multiWordProperty = DummyConfigConstants.Default.MULTI_WORD_PROPERTY;
    private BigDecimal bigDecimal = new BigDecimal(DummyConfigConstants.Default.BIG_DECIMAL);
    private LocalDateTime time = DummyConfigConstants.Default.TIME;

    public DummyConfigEntityBuilder withMultiWordProperty(String multiWordProperty) {
        this.multiWordProperty = multiWordProperty;
        return this;
    }

    public DummyConfigEntityBuilder withBigDecimal(BigDecimal bigDecimal) {
        this.bigDecimal = bigDecimal;
        return this;
    }

    public DummyConfigEntityBuilder withTime(LocalDateTime time) {
        this.time = time;
        return this;
    }

    public DummyConfigEntity build() {
        DummyConfigEntity entity = new DummyConfigEntity();
        entity.setMultiWordProperty(multiWordProperty);
        entity.setBigDecimal(bigDecimal);
        entity.setTime(time);
        return entity;
    }

    public static DummyConfigEntityBuilder defaultEntity() {
        return new DummyConfigEntityBuilder();
    }
} 