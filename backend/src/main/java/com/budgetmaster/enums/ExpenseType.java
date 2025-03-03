package com.budgetmaster.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ExpenseType {
	RECURRING,
	ONE_TIME;
	
	@JsonCreator
	public static ExpenseType fromString(String value) {
		return ExpenseType.valueOf(value.toUpperCase());
	}
	
	@JsonValue
	public String toJson() {
		return name();
	}
}