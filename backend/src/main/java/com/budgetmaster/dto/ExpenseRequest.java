package com.budgetmaster.dto;

import com.budgetmaster.enums.TransactionType;
import com.budgetmaster.constants.validation.ValidationMessages.ExpenseValidation;
import com.budgetmaster.constants.validation.ValidationMessages.CommonValidation;
import com.budgetmaster.constants.validation.ValidationPatterns;
import com.budgetmaster.dto.money.MoneyRequest;
import com.budgetmaster.enums.ExpenseCategory;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class ExpenseRequest {
	@NotBlank(message = ExpenseValidation.VALIDATION_MESSAGE_REQUIRED_NAME)
	private String name;
	
	@NotNull(message = CommonValidation.VALIDATION_MESSAGE_REQUIRED_MONEY)
	@Valid
	private MoneyRequest money;
	
	@NotNull(message = ExpenseValidation.VALIDATION_MESSAGE_REQUIRED_CATEGORY)
	private ExpenseCategory category;
	
	@NotNull(message = CommonValidation.VALIDATION_MESSAGE_REQUIRED_TYPE)
	private TransactionType type;
	
	@NotNull(message = CommonValidation.VALIDATION_MESSAGE_REQUIRED_MONTH)
	@Pattern(regexp = ValidationPatterns.VALIDATION_PATTERN_YEAR_MONTH_REGEX, 
			message = CommonValidation.VALIDATION_MESSAGE_INVALID_MONTH)
	private String month;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public MoneyRequest getMoney() {
		return money;
	}
	
	public void setMoney(MoneyRequest money) {
		this.money = money;
	}
	
	public ExpenseCategory getCategory() {
		return category;
	}
	
	public void setCategory(ExpenseCategory category) {
		this.category = category;
	}
	
	public TransactionType getType() {
		return type;
	}
	
	public void setType(TransactionType type) {
		this.type = type;
	}
	
	public String getMonth() {
		return month;
	}
	
	public void setMonth(String month) {
		this.month = month;
	}
}