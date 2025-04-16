package com.budgetmaster.dto;

import com.budgetmaster.dto.money.MoneyRequest;
import com.budgetmaster.enums.TransactionType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class IncomeRequest {
	@NotBlank(message = "Income name is required.")
	private String name;
	
	@NotBlank(message = "Income source is required.")
	private String source;

	@NotNull(message = "Money details (amount and currency) are required.")
	@Valid
	private MoneyRequest money;
	
	@NotNull(message = "Income transaction type is required.")
	private TransactionType type;
	
	@NotNull(message = "Month is required.")
	@Pattern(regexp = "^\\d{4}-(?:0[1-9]|1[0-2])$", message = "Month must be in format YYYY-MM.")
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