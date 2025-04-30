package com.budgetmaster.repository;

import com.budgetmaster.model.Expense;

import java.time.YearMonth;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
	List<Expense> findByMonth(YearMonth month);
}