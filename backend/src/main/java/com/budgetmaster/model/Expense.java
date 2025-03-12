package com.budgetmaster.model;

import com.budgetmaster.enums.TransactionType;
import com.budgetmaster.enums.ExpenseCategory;

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
	private Double amount;
	
	@Enumerated(EnumType.STRING)
	private ExpenseCategory category;
	
	@Enumerated(EnumType.STRING)
	private TransactionType transactionType;
	
	public Expense() {}
	
	public Expense(String name, Double amount, ExpenseCategory category, TransactionType transactionType) {
		this.name = name;
		this.transactionType = transactionType;
		this.category = category;
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
	
	public Double getAmount() {
		return amount;
	}
	
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	
	public ExpenseCategory getExpenseCategory() {
		return category;
	}
	
	public void setExpenseCategory(ExpenseCategory category) {
		this.category = category;
	}
	
	public TransactionType getTransactionType() {
		return transactionType;
	}
	
	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}
}