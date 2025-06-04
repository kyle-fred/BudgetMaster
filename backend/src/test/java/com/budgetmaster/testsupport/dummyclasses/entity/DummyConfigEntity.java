package com.budgetmaster.testsupport.dummyclasses.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DummyConfigEntity {
    
    private String multiWordProperty;
    private BigDecimal bigDecimal;
    private LocalDateTime time;

    public String getMultiWordProperty() {
        return multiWordProperty;
    }

    public void setMultiWordProperty(String multiWordProperty) {
        this.multiWordProperty = multiWordProperty;
    }

    public BigDecimal getBigDecimal() {
        return bigDecimal;
    }

    public void setBigDecimal(BigDecimal bigDecimal) {
        this.bigDecimal = bigDecimal;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
} 