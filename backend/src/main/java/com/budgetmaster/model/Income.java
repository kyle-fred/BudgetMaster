package com.budgetmaster.model;

import com.budgetmaster.constants.database.ColumnNames.CommonColumns;
import com.budgetmaster.constants.database.ColumnNames.IncomeColumns;
import com.budgetmaster.constants.database.ColumnNames.TransactionColumns;
import com.budgetmaster.constants.database.TableNames;
import com.budgetmaster.constants.date.DateFormats;

import java.time.LocalDateTime;
import java.time.YearMonth;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.budgetmaster.enums.TransactionType;
import com.budgetmaster.model.value.Money;
import com.fasterxml.jackson.annotation.JsonFormat;

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
@Table(name = TableNames.TABLE_NAME_INCOMES)
public class Income {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = CommonColumns.COLUMN_NAME_ID)
	private Long id;
	
	@Column(name = TransactionColumns.COLUMN_NAME_TRANSACTION_NAME, nullable = false)
	private String name;
	
	@Column(name = IncomeColumns.COLUMN_NAME_SOURCE, nullable = false)
	private String source;
	
	@Embedded
	private Money money;
	
	@Enumerated(EnumType.STRING)
	@Column(name = TransactionColumns.COLUMN_NAME_TYPE, nullable = false)
	private TransactionType type;
	
	@Column(name = CommonColumns.COLUMN_NAME_MONTH, nullable = false)
 	private YearMonth month;
 	
	@CreationTimestamp
	@JsonFormat(pattern = DateFormats.DATE_FORMATS_DATE_TIME_STANDARD)
	@Column(name = CommonColumns.COLUMN_NAME_CREATED_AT, nullable = false, updatable = false, insertable = false)
 	private LocalDateTime createdAt;
 	
	@UpdateTimestamp
	@JsonFormat(pattern = DateFormats.DATE_FORMATS_DATE_TIME_STANDARD)
	@Column(name = CommonColumns.COLUMN_NAME_LAST_UPDATED_AT, nullable = false, insertable = false)
 	private LocalDateTime lastUpdatedAt;
	
	public Income() {}
	
	public Income(String name, String source, Money money, TransactionType type, YearMonth month) {
		this.name = name;
		this.source = source;
		this.money = money;
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
	
	public String getSource() {
		return source;
	}
	
	public void setSource(String source) {
		this.source = source;
	}
	
	public Money getMoney() {
		return money;
	}
	
	public void setMoney(Money money) {
		this.money = money;
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