package com.budgetmaster.model;

import com.budgetmaster.enums.ExpenseType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Expense {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	private String target;
	private Double amount;
	
	@Enumerated(EnumType.STRING)
	private ExpenseType expenseType;
	
	public Expense() {}
	
	public Expense(String name, String target, Double amount, ExpenseType expenseType) {
		this.name = name;
		this.target = target;
		this.expenseType = expenseType;
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
	
	public String getTarget() {
		return target;
	}
	
	public void setTarget(String target) {
		this.target = target;
	}
	
	public Double getAmount() {
		return amount;
	}
	
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	
	public ExpenseType getExpenseType() {
		return expenseType;
	}
	
	public void setExpenseType(ExpenseType type) {
		this.expenseType = type;
	}
}