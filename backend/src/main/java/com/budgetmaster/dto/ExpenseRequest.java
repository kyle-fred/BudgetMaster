package com.budgetmaster.dto;

import com.budgetmaster.enums.TransactionType;
import com.budgetmaster.dto.money.MoneyRequest;
import com.budgetmaster.enums.ExpenseCategory;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class ExpenseRequest {
	@NotBlank(message = "Expense name is required.")
	private String name;
	
	@NotNull(message = "Money details (amount and currency) are required.")
	@Valid
	private MoneyRequest money;
	
	@NotNull(message = "Expense category is required.")
	private ExpenseCategory category;
	
	@NotNull(message = "Expense transaction type is required.")
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