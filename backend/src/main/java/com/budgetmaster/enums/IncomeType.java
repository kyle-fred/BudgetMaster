package com.budgetmaster.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum IncomeType {
	RECURRING,
	ONE_TIME;
	
	@JsonCreator
	public static IncomeType fromString(String value) {
		return IncomeType.valueOf(value.toUpperCase());
	}
	
	@JsonValue
	public String toJson() {
		return name();
	}
}