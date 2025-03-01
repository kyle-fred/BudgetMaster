package com.budgetmaster.model;

import com.budgetmaster.enums.IncomeType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Income {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	private String source;
	private Double amount;
	
	@Enumerated(EnumType.STRING)
	private IncomeType incomeType;
	
	public Income() {}
	
	public Income(String name, String source, Double amount, IncomeType incomeType) {
		this.name = name;
		this.source = source;
		this.incomeType = incomeType;
		this.amount = amount;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
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
	
	public void setIncomeType(IncomeType type) {
		this.incomeType = type;
	}
}