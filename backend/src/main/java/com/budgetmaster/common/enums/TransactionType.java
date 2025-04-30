package com.budgetmaster.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TransactionType {
	RECURRING,
	ONE_TIME;
	
	@JsonCreator
	public static TransactionType fromString(String value) {
		return TransactionType.valueOf(value.toUpperCase());
	}
	
	@JsonValue
	public String toJson() {
		return name();
	}
}