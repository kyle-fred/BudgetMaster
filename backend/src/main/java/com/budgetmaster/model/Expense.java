package com.budgetmaster.model;

import com.budgetmaster.constants.database.ColumnNames;
import com.budgetmaster.constants.database.TableNames;
import com.budgetmaster.constants.date.DateFormats;
import com.budgetmaster.enums.TransactionType;
import com.budgetmaster.model.value.Money;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.time.YearMonth;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.budgetmaster.enums.ExpenseCategory;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = TableNames.EXPENSES)
public class Expense {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = ColumnNames.Common.ID)
	private Long id;
	
	@Column(name = ColumnNames.Transaction.NAME, nullable = false)
	private String name;
	
	@Embedded
	private Money money;
	
	@Enumerated(EnumType.STRING)
	@Column(name = ColumnNames.Expense.CATEGORY, nullable = false)
	private ExpenseCategory category;
	
	@Enumerated(EnumType.STRING)
	@Column(name = ColumnNames.Transaction.TYPE, nullable = false)
	private TransactionType type;
	
	@Column(name = ColumnNames.Common.MONTH, nullable = false)
  	private YearMonth month;
  	
	@CreationTimestamp
	@JsonFormat(pattern = DateFormats.STANDARD_DATE_TIME)
	@Column(name = ColumnNames.Common.CREATED_AT, nullable = false, updatable = false, insertable = false)
  	private LocalDateTime createdAt;
  	
	@UpdateTimestamp
	@JsonFormat(pattern = DateFormats.STANDARD_DATE_TIME)
	@Column(name = ColumnNames.Common.LAST_UPDATED_AT, nullable = false, insertable = false)
  	private LocalDateTime lastUpdatedAt;
	
	public Expense() {}
	
	public Expense(String name, Money money, ExpenseCategory category, TransactionType type, YearMonth month) {
		this.name = name;
		this.money = money;
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
	
	public Money getMoney() {
		return money;
	}
	
	public void setMoney(Money money) {
		this.money = money;
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