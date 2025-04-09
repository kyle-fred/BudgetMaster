package com.budgetmaster.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class BudgetRequest {
	
	@NotNull(message = "Total income is required.")
	@Min(value = 0, message = "Total income cannot be negative.")
	private Double totalIncome;
	
	@NotNull(message = "Total expense is required.")
	@Min(value = 0, message = "Total expense cannot be negative.")
	private Double totalExpense;
	
	@Pattern(regexp = "^\\d{4}-(?:0[1-9]|1[0-2])$", message = "Month year must be in format YYYY-MM")
	private String monthYear;
	
	public Double getTotalIncome() {
		return totalIncome;
	}
	
	public void setTotalIncome(Double totalIncome) {
		this.totalIncome = totalIncome;
	}
	
	public Double getTotalExpense() {
		return totalExpense;
	}
	
	public void setTotalExpense(Double totalExpense) {
		this.totalExpense = totalExpense;
	}
	
	public String getMonthYear() {
		return monthYear;
	}
	
	public void setMonthYear(String monthYear) {
		this.monthYear = monthYear;
	}
}