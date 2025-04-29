package com.budgetmaster.dto;

import com.budgetmaster.constants.validation.ValidationMessages;
import com.budgetmaster.constants.validation.ValidationPatterns;
import com.budgetmaster.dto.money.MoneyRequest;
import com.budgetmaster.enums.TransactionType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class IncomeRequest {
	@NotBlank(message = ValidationMessages.Income.REQUIRED_NAME)
	private String name;
	
	@NotBlank(message = ValidationMessages.Income.REQUIRED_SOURCE)
	private String source;

	@NotNull(message = ValidationMessages.Common.REQUIRED_MONEY)
	@Valid
	private MoneyRequest money;
	
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