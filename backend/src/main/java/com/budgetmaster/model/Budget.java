package com.budgetmaster.model;

import com.budgetmaster.constants.database.ColumnNames.BudgetColumns;
import com.budgetmaster.constants.database.ColumnNames.CommonColumns;
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
@Table(name = TableNames.TABLE_NAME_BUDGETS)
public class Budget {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = CommonColumns.COLUMN_NAME_ID)
	private Long id;
	
	@Column(name = BudgetColumns.COLUMN_NAME_TOTAL_INCOME, precision = 19, scale = 2)
	private BigDecimal totalIncome;
	
	@Column(name = BudgetColumns.COLUMN_NAME_TOTAL_EXPENSE, precision = 19, scale = 2)
	private BigDecimal totalExpense;
	
	@Column(name = BudgetColumns.COLUMN_NAME_SAVINGS, precision = 19, scale = 2, insertable = false, updatable = false)
	private BigDecimal savings;

	@Column(name = BudgetColumns.COLUMN_NAME_COMMON_CURRENCY, nullable = false, length = 3)
	private Currency currency;
	
	@Column(name = CommonColumns.COLUMN_NAME_MONTH, nullable = false, unique=true)
	private YearMonth month;
	
	@CreationTimestamp
	@Column(name = CommonColumns.COLUMN_NAME_CREATED_AT, nullable = false, updatable = false, insertable = false)
	@JsonFormat(pattern = DateFormats.DATE_FORMATS_DATE_TIME_STANDARD)
	private LocalDateTime createdAt;
	
	@UpdateTimestamp
	@Column(name = CommonColumns.COLUMN_NAME_LAST_UPDATED_AT, nullable = false, insertable = false)
	@JsonFormat(pattern = DateFormats.DATE_FORMATS_DATE_TIME_STANDARD)
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