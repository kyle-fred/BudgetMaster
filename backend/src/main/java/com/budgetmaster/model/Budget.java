package com.budgetmaster.model;

import java.time.LocalDateTime;
import java.time.YearMonth;

import org.springframework.beans.factory.annotation.Autowired;

import com.budgetmaster.repository.ExpenseRepository;
import com.budgetmaster.repository.IncomeRepository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

@Entity
public class Budget {
	
    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double income;
    private double expenses;
    private double savings;

    @Column(nullable = false)
    private YearMonth monthYear;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime lastUpdatedAt;

    public Budget(YearMonth monthYear) {
        this.monthYear = monthYear;
    }
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public double getIncome() {
		return incomeRepository.sumByMonthYear(this.monthYear);
	}
	
	public double getExpenses() {
		return expenseRepository.sumByMonthYear(this.monthYear);
	}
	
	public double getSavings() {
		return getIncome() - getExpenses();
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