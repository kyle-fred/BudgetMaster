package com.budgetmaster.dto;

import com.budgetmaster.enums.TransactionType;
import com.budgetmaster.enums.ExpenseCategory;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class ExpenseRequest {
	
	@NotBlank(message = "Expense name is required.")
	private String name;
	
	@NotNull(message = "Expense amount is required.")
	@Min(value = 0, message = "Expense amount cannot be negative.")
	private Double amount;
	
	@NotNull(message = "Expense category is required.")
	private ExpenseCategory category;
	
	@NotNull(message = "Expense transaction type is required.")
	private TransactionType type;
	
	@Pattern(regexp = "^\\d{4}-(?:0[1-9]|1[0-2])$", message = "Month year must be in format YYYY-MM")
	private String monthYear;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Double getAmount() {
		return amount;
	}
	
	public void setAmount(Double amount) {
		this.amount = amount;
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
	
	public String getMonthYear() {
		return monthYear;
	}
	
	public void setMonthYear(String monthYear) {
		this.monthYear = monthYear;
	}
}