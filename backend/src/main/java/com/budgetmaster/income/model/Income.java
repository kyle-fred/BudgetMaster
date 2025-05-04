package com.budgetmaster.income.model;

import com.budgetmaster.common.constants.database.ColumnNames;
import com.budgetmaster.common.constants.database.TableNames;
import com.budgetmaster.common.constants.date.DateFormats;
import com.budgetmaster.common.enums.TransactionType;
import com.budgetmaster.common.utils.DateUtils;
import com.budgetmaster.income.dto.IncomeRequest;
import com.budgetmaster.money.model.Money;

import java.time.LocalDateTime;
import java.time.YearMonth;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
@Table(name = TableNames.INCOMES)
public class Income {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = ColumnNames.Common.ID)
	private Long id;
	
	@Column(name = ColumnNames.Transaction.NAME, nullable = false)
	private String name;
	
	@Column(name = ColumnNames.Income.SOURCE, nullable = false)
	private String source;
	
	@Embedded
	private Money money;
	
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
	
	public Income() {}
	
	public Income(String name, String source, Money money, TransactionType type, YearMonth month) {
		this.name = name;
		this.source = source;
		this.money = money;
		this.type = type;
		this.month = month;
	}

	public static Income from(IncomeRequest request) {
		return new Income(
			request.getName().toUpperCase(),
			request.getSource().toUpperCase(),
			Money.of(request.getMoney().getAmount(), request.getMoney().getCurrency()),
			request.getType(),
			DateUtils.getValidYearMonth(request.getMonth())
		);
	}

	public void updateFrom(IncomeRequest request) {
		this.name = request.getName().toUpperCase();
		this.source = request.getSource().toUpperCase();
		this.money = Money.of(request.getMoney().getAmount(), request.getMoney().getCurrency());
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

	public Income deepCopy() {
		Income copy = new Income();
        copy.id = this.id;
        copy.name = this.name;
        copy.source = this.source;
        copy.type = this.type;
        copy.month = this.month;
        copy.money = Money.of(this.money.getAmount(), this.money.getCurrency());
        copy.createdAt = this.createdAt;
        copy.lastUpdatedAt = this.lastUpdatedAt;
        return copy;
	}
}