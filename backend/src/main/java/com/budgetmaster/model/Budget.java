package com.budgetmaster.model;

public class Budget {
	private double income;
	private double expenses;
	private double savings;
	
	public Budget() {}
	
	public Budget(double income, double expenses) {
		this.income = income;
		this.expenses = expenses;
		this.savings = income - expenses;
	}
	
	public double getIncome() {
		return income;
	}
	public void setIncome(double income) {
		this.income = income;
	}
	
	public double getExpenses() {
		return expenses;
	}
	public void setExpenses(double expenses) {
		this.expenses = expenses;
	}
	
	public double getSavings() {
		return savings;
	}
	public void setSavings(double savings) {
		this.savings = savings;
	}
}