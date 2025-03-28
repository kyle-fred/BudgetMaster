package com.budgetmaster.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class BudgetRequest {
	
	@NotNull(message = "Income is required.")
	@Min(value = 0, message = "Income cannot be negative.")
	private Double totalIncome;
	
	@NotNull(message = "Expenses are required.")
	@Min(value = 0, message = "Expenses cannot be negative.")
	private Double expenses;
	
	@Pattern(regexp = "^\\d{4}-\\d{2}$", message = "Invalid monthYear value. Expected format: YYYY-MM.")
	private String monthYear;
	
	public Double getTotalIncome() {
		return totalIncome;
	}
	
	public void setTotalIncome(Double totalIncome) {
		this.totalIncome = totalIncome;
	}
	
	public Double getExpenses() {
		return expenses;
	}
	
	public void setExpenses(Double expenses) {
		this.expenses = expenses;
	}
	
	public String getMonthYear() {
		return monthYear;
	}
	
	public void setMonthYear(String monthYear) {
		this.monthYear = monthYear;
	}
}