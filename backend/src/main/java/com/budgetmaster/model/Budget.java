package com.budgetmaster.model;

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

@Entity
public class Budget {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private double totalIncome;
	
	private double expenses;
	
	private double savings;
	
	@Column(nullable = false, unique=true)
	private YearMonth monthYear;
	
	@CreationTimestamp
	@Column(nullable = false, updatable = false, insertable = false)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdAt;
	
	@UpdateTimestamp
	@Column(nullable = false, insertable = false)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime lastUpdatedAt;
	
	public Budget() {}
	
	public Budget(double totalIncome, double expenses, YearMonth monthYear) {
		this.totalIncome = totalIncome;
		this.expenses = expenses;
		this.savings = totalIncome - expenses;
		this.monthYear = monthYear;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public double getTotalIncome() {
		return totalIncome;
	}
	
	public void setTotalIncome(double totalIncome) {
		this.totalIncome = totalIncome;
	}
	
	public double getExpenses() {
		return expenses;
	}
	
	public void setExpenses(double expenses) {
		this.expenses = expenses;
	}
	
	public double getSavings() {
		return savings;
	}
	
	public void setSavings(double savings) {
		this.savings = savings;
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