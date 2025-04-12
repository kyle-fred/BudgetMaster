package com.budgetmaster.model;

import com.budgetmaster.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.time.YearMonth;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.budgetmaster.enums.ExpenseCategory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;

@Entity
@Table(name = "EXPENSE")
public class Expense {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "NAME")
	private String name;
	
	@Min(value = 0, message = "Amount must be greater than 0")
	@Column(name = "AMOUNT")
	private Double amount;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "CATEGORY")
	private ExpenseCategory category;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "TYPE")
	private TransactionType type;
	
	@Column(name = "MONTH", nullable = false)
  	private YearMonth month;
  	
	@CreationTimestamp
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "CREATED_AT", nullable = false, updatable = false, insertable = false)
  	private LocalDateTime createdAt;
  	
	@UpdateTimestamp
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "LAST_UPDATED_AT", nullable = false, insertable = false)
  	private LocalDateTime lastUpdatedAt;
	
	public Expense() {}
	
	public Expense(String name, Double amount, ExpenseCategory category, TransactionType type, YearMonth month) {
		this.name = name;
		this.amount = amount;
		this.category = category;
		this.type = type;
		this.month = month;
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
	
 	public YearMonth getMonth() {
  		return month;
  	}
  	
  	public void setMonth(YearMonth month) {
  		this.month = month;
  	}
  	
  	public LocalDateTime getCreatedAt() {
  		return createdAt;
  	}
  	
  	public LocalDateTime getLastUpdatedAt() {
  		return lastUpdatedAt;
  	}
}