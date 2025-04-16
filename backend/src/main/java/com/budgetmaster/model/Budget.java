package com.budgetmaster.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "BUDGET")
public class Budget {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "TOTAL_INCOME", precision = 19, scale = 2)
	private BigDecimal totalIncome;
	
	@Column(name = "TOTAL_EXPENSE", precision = 19, scale = 2)
	private BigDecimal totalExpense;
	
	@Column(name = "SAVINGS", precision = 19, scale = 2, insertable = false, updatable = false)
	private BigDecimal savings;
	
	@Column(name = "MONTH", nullable = false, unique=true)
	private YearMonth month;
	
	@CreationTimestamp
	@Column(name = "CREATED_AT", nullable = false, updatable = false, insertable = false)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdAt;
	
	@UpdateTimestamp
	@Column(name = "LAST_UPDATED_AT", nullable = false, insertable = false)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime lastUpdatedAt;
	
	protected Budget() {}
	
	public Budget(YearMonth month) {
		this.month = month;
		this.totalIncome = BigDecimal.ZERO;
		this.totalExpense = BigDecimal.ZERO;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public BigDecimal getTotalIncome() {
		return totalIncome;
	}
	
	public void setTotalIncome(BigDecimal totalIncome) {
		this.totalIncome = totalIncome;
	}
	
	public BigDecimal getTotalExpense() {
		return totalExpense;
	}
	
	public void setTotalExpense(BigDecimal totalExpense) {
		this.totalExpense = totalExpense;
	}
	
	public BigDecimal getSavings() {
		return savings;
	}
	
	public void setSavings(BigDecimal savings) {
		this.savings = savings;
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