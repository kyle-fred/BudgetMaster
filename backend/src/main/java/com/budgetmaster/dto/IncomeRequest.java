package com.budgetmaster.dto;

import com.budgetmaster.enums.IncomeType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class IncomeRequest {
	
	@NotBlank(message = "Income name is required.")
	private String name;
	
	@NotBlank(message = "Income source is required.")
	private String source;
	
	@NotNull(message = "Income amount is required.")
	@Min(value = 0, message = "Income amount cannot be negative.")
	private Double amount;
	
	@NotNull(message = "Income type is required.")
	private IncomeType incomeType;
	
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
	
	public IncomeType getIncomeType() {
		return incomeType;
	}
	
	public void setIncomeType(IncomeType incomeType) {
		this.incomeType = incomeType;
	}
}