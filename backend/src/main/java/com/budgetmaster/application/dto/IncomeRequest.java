package com.budgetmaster.application.dto;

import com.budgetmaster.application.enums.TransactionType;
import com.budgetmaster.constants.validation.ValidationMessages;
import com.budgetmaster.constants.validation.ValidationPatterns;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class IncomeRequest {
	@NotBlank(message = ValidationMessages.Income.NAME_IS_REQUIRED)
	private String name;
	
	@NotBlank(message = ValidationMessages.Income.SOURCE_IS_REQUIRED)
	private String source;

	@NotNull(message = ValidationMessages.MONEY_DETAILS_ARE_REQUIRED)
	@Valid
	private MoneyRequest money;
	
	@NotNull(message = ValidationMessages.TYPE_IS_REQUIRED)
	private TransactionType type;
	
	@NotNull(message = ValidationMessages.MONTH_IS_REQUIRED)
	@Pattern(regexp = ValidationPatterns.Date.YEAR_MONTH_REGEX, 
			message = ValidationMessages.INVALID_MONTH_FORMAT)
	private String month;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getSource() {
		return source;
	}
	
	public void setSource(String source) {
		this.source = source;
	}
	
	public MoneyRequest getMoney() {
		return money;
	}
	
	public void setMoney(MoneyRequest money) {
		this.money = money;
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