package com.budgetmaster.model;

import java.time.LocalDateTime;
import java.time.YearMonth;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.budgetmaster.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Income {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	
	private String source;
	
	private Double amount;
	
	@Enumerated(EnumType.STRING)
	private TransactionType type;
	
	@Column(nullable = false)
 	private YearMonth monthYear;
 	
	@CreationTimestamp
	@Column(nullable = false, updatable = false, insertable = false)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
 	private LocalDateTime createdAt;
 	
	@UpdateTimestamp
	@Column(nullable = false, insertable = false)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
 	private LocalDateTime lastUpdatedAt;
	
	public Income() {}
	
	public Income(String name, String source, Double amount, TransactionType type, YearMonth monthYear) {
		this.name = name;
		this.source = source;
		this.amount = amount;
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
	
	public String getSource() {
		return source;
	}
	
	public void setSource(String source) {
		this.source = source;
	}
	
	public Double getAmount() {
		return amount;
	}
	
	public void setAmount(Double amount) {
		this.amount = amount;
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
 	
 	public LocalDateTime getLastUpdatedAt() {
 		return lastUpdatedAt;
 	}
}