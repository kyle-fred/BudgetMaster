package com.budgetmaster.dto;

import com.budgetmaster.enums.TransactionType;
import com.budgetmaster.enums.ExpenseCategory;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ExpenseRequest {
	
	@NotBlank(message = "Expense name is required.")
	private String name;
	
	@NotNull(message = "Expense amount is required.")
	@Min(value = 0, message = "Expense amount cannot be negative.")
	private Double amount;
	
	@NotNull(message = "Expense category is required.")
	private ExpenseCategory expenseCategory;
	
	@NotNull(message = "Expense's transaction type is required.")
	private TransactionType transactionType;
	
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
		return expenseCategory;
	}
	
	public void setExpenseCategory(ExpenseCategory expenseCategory) {
		this.expenseCategory = expenseCategory;
	}
	
	public TransactionType getTransactionType() {
		return transactionType;
	}
	
	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}
}