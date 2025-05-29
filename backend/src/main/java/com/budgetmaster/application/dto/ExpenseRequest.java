package com.budgetmaster.application.dto;

import com.budgetmaster.application.enums.ExpenseCategory;
import com.budgetmaster.application.enums.TransactionType;
import com.budgetmaster.common.constants.validation.ValidationMessages;
import com.budgetmaster.common.constants.validation.ValidationPatterns;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class ExpenseRequest {
	@NotBlank(message = ValidationMessages.Expense.REQUIRED_NAME)
	private String name;
	
	@NotNull(message = ValidationMessages.Common.REQUIRED_MONEY)
	@Valid
	private MoneyRequest money;
	
	@NotNull(message = ValidationMessages.Expense.REQUIRED_CATEGORY)
	private ExpenseCategory category;
	
	@NotNull(message = ValidationMessages.Common.REQUIRED_TYPE)
	private TransactionType type;
	
	@NotNull(message = ValidationMessages.Common.REQUIRED_MONTH)
	@Pattern(regexp = ValidationPatterns.Date.YEAR_MONTH_REGEX, 
			message = ValidationMessages.Common.INVALID_MONTH_FORMAT)
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