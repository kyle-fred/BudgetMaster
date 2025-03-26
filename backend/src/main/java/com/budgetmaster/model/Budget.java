package com.budgetmaster.model;

import java.time.LocalDateTime;
import java.time.YearMonth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

@Entity
public class Budget {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private double income;
	
	private double expenses;
	
	private double savings;
	
	@Column(nullable = false, unique=true)
	private YearMonth monthYear;
	
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;
	
	@Column(nullable = false)
	private LocalDateTime lastUpdatedAt;
	
	public Budget() {}
	
	public Budget(double income, double expenses, YearMonth monthYear) {
		this.income = income;
		this.expenses = expenses;
		this.savings = income - expenses;
		this.monthYear = monthYear;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
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
	
	public YearMonth getMonthYear() {
		return monthYear;
	}
	
	public void setMonthYear(YearMonth monthYear) {
		this.monthYear = monthYear;
	}
	
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	
    public LocalDateTime getLastUpdatedAt() {
        return lastUpdatedAt;
    }
    
    public void setLastUpdatedAt(LocalDateTime lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }
    
    @PrePersist
    public void prePersist() {
    	LocalDateTime now = LocalDateTime.now().withNano(0);
    	this.createdAt = now;
    	this.lastUpdatedAt = now;
    }
    
    @PreUpdate
    public void preUpdate() {
    	this.lastUpdatedAt = LocalDateTime.now().withNano(0);
    }
}