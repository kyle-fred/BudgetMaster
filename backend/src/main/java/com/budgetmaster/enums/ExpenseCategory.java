package com.budgetmaster.enums;

import com.budgetmaster.constants.common.StringConstants;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ExpenseCategory {
	HOUSING,
	UTILITIES,
	TAXES,
	MOBILE_PHONE,
	GROCERIES,
	DINING_OUT,
	TRANSPORT,
	HEALTH,
	FITNESS,
	DEBT_REPAYMENT,
	SUBSCIPTIONS,
	HOBBIES,
	EVENTS,
	CLOTHING_AND_ACCESSORIES,
	ELECTRONICS,
	HOME_AND_DECOR,
	EDUCATION,
	GIFTS_AND_DONATIONS,
	PETS,
	MISCELLANEOUS;
	
	@JsonCreator
	public static ExpenseCategory fromString(String value) {
		return ExpenseCategory.valueOf(value.toUpperCase().replace(StringConstants.STRING_CONSTANTS_SPACE, StringConstants.STRING_CONSTANTS_UNDERSCORE));
	}
	
	@JsonValue
	public String toJson() {
		return name();
	}
}