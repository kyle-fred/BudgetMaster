package com.budgetmaster.model;

import com.budgetmaster.enums.TransactionType;

import java.time.LocalDateTime;
import java.time.YearMonth;

import com.budgetmaster.enums.ExpenseCategory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

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
	private TransactionType type;
	
	@Column(nullable = false)
  	private YearMonth monthYear;
  	
  	@Column(nullable = false, updatable = false)
  	private LocalDateTime createdAt;
  	
  	@Column(nullable = false)
  	private LocalDateTime lastUpdatedAt;
 	
	
	public Expense() {}
	
	public Expense(String name, Double amount, ExpenseCategory category, TransactionType type, YearMonth monthYear) {
		this.name = name;
		this.amount = amount;
		this.category = category;
		this.type = type;
		this.monthYear = monthYear;
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
	
	public ExpenseCategory getCategory() {
		return category;
	}
	
	public void setCategory(ExpenseCategory category) {
		this.category = category;
	}
	
	public TransactionType getType() {
		return type;
	}
	
	public void setType(TransactionType type) {
		this.type = type;
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