package com.budgetmaster.model;

import com.budgetmaster.constants.database.ColumnConstraints;
import com.budgetmaster.constants.database.ColumnNames;
import com.budgetmaster.constants.database.TableNames;
import com.budgetmaster.constants.date.DateFormats;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Currency;

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
@Table(name = TableNames.BUDGETS)
public class Budget {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = ColumnNames.Common.ID)
	private Long id;
	
	@Column(name = ColumnNames.Budget.TOTAL_INCOME, 
			precision = ColumnConstraints.Amount.PRECISION, scale = ColumnConstraints.Amount.SCALE)
	private BigDecimal totalIncome;
	
	@Column(name = ColumnNames.Budget.TOTAL_EXPENSE, 
			precision = ColumnConstraints.Amount.PRECISION, scale = ColumnConstraints.Amount.SCALE)
	private BigDecimal totalExpense;
	
	@Column(name = ColumnNames.Budget.SAVINGS, 
			precision = ColumnConstraints.Amount.PRECISION, scale = ColumnConstraints.Amount.SCALE, 
			insertable = false, updatable = false)
	private BigDecimal savings;

	@Column(name = ColumnNames.Budget.COMMON_CURRENCY, nullable = false, length = 3)
	private Currency currency;
	
	@Column(name = ColumnNames.Common.MONTH, nullable = false, unique=true)
	private YearMonth month;
	
	@CreationTimestamp
	@Column(name = ColumnNames.Common.CREATED_AT, nullable = false, updatable = false, insertable = false)
	@JsonFormat(pattern = DateFormats.STANDARD_DATE_TIME)
	private LocalDateTime createdAt;
	
	@UpdateTimestamp
	@Column(name = ColumnNames.Common.LAST_UPDATED_AT, nullable = false, insertable = false)
	@JsonFormat(pattern = DateFormats.STANDARD_DATE_TIME)
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

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}
}