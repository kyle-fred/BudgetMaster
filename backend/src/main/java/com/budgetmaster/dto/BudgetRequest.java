package com.budgetmaster.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class BudgetRequest {
	
	@NotNull(message = "Income is required.")
	@Min(value = 0, message = "Income cannot be negative.")
	private Double income;
	
	@NotNull(message = "Expenses are required.")
	@Min(value = 0, message = "Expenses cannot be negative.")
	private Double expenses;
	
	public Double getIncome() {
		return income;
	}
	public void setIncome(Double income) {
		this.income = income;
	}
	
	public Double getExpenses() {
		return expenses;
	}
	public void setExpenses(Double expenses) {
		this.expenses = expenses;
	}
}