package com.budgetmaster.application.model;

import com.budgetmaster.application.dto.ExpenseRequest;
import com.budgetmaster.application.enums.ExpenseCategory;
import com.budgetmaster.application.enums.TransactionType;
import com.budgetmaster.application.util.DateUtils;
import com.budgetmaster.constants.database.ColumnNames;
import com.budgetmaster.constants.database.TableNames;

import java.time.LocalDateTime;
import java.time.YearMonth;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
	@Column(name = ColumnNames.Audit.ID)
	private Long id;
	
	@Column(name = ColumnNames.Expense.NAME, nullable = false)
	private String name;
	
	@Embedded
	private Money money;
	
	@Enumerated(EnumType.STRING)
	@Column(name = ColumnNames.Expense.CATEGORY, nullable = false)
	private ExpenseCategory category;
	
	@Enumerated(EnumType.STRING)
	@Column(name = ColumnNames.Expense.TYPE, nullable = false)
	private TransactionType type;
	
	@Column(name = ColumnNames.Expense.MONTH, nullable = false)
  	private YearMonth month;
  	
	@CreationTimestamp
	@Column(name = ColumnNames.Audit.CREATED_AT, nullable = false, updatable = false, insertable = false)
  	private LocalDateTime createdAt;
  	
	@UpdateTimestamp
	@Column(name = ColumnNames.Audit.LAST_UPDATED_AT, nullable = false, insertable = false)
  	private LocalDateTime lastUpdatedAt;
	
	protected Expense() {}

	public static Expense of(String name, Money money, ExpenseCategory category, TransactionType type, YearMonth month) {
		Expense expense = new Expense();
		expense.name = name;
		expense.money = money;
		expense.category = category;
		expense.type = type;
		expense.month = month;
		return expense;
	}

	public static Expense from(ExpenseRequest request) {
		return of(
			request.getName().toUpperCase(),
			Money.of(request.getMoney().getAmount(), request.getMoney().getCurrency()),
			request.getCategory(),
			request.getType(),
			DateUtils.getValidYearMonth(request.getMonth())
		);
	}

	public void updateFrom(ExpenseRequest request) {
		this.name = request.getName().toUpperCase();
		this.money = Money.of(request.getMoney().getAmount(), request.getMoney().getCurrency());
		this.category = request.getCategory();
		this.type = request.getType();
		if (request.getMonth() != null && !request.getMonth().isEmpty()) {
			this.month = DateUtils.getValidYearMonth(request.getMonth());
		}
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

	public Expense deepCopy() {
		Expense copy = new Expense();
		copy.id = this.id;
		copy.name = this.name;
		copy.money = this.money;
		copy.category = this.category;
		copy.type = this.type;
		copy.month = this.month;
		copy.createdAt = this.createdAt;
		copy.lastUpdatedAt = this.lastUpdatedAt;
		return copy;
	}
}