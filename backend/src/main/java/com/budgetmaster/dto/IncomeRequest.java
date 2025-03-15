package com.budgetmaster.dto;

import com.budgetmaster.enums.TransactionType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class IncomeRequest {
	
	@NotBlank(message = "Income name is required.")
	private String name;
	
	@NotBlank(message = "Income source is required.")
	private String source;
	
	@NotNull(message = "Income amount is required.")
	@Min(value = 0, message = "Income amount cannot be negative.")
	private Double amount;
	
	@NotNull(message = "Income transaction type is required.")
	private TransactionType type;
	
	@Pattern(regexp = "^\\d{4}-\\d{2}$", message = "Invalid monthYear value. Expected format: YYYY-MM.")
	private String monthYear;
	
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
	
	public Double getAmount() {
		return amount;
	}
	
	public void setAmount(Double amount) {
		this.amount = amount;
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