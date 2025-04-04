package com.budgetmaster.repository;

import com.budgetmaster.model.Expense;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
	List<Expense> findByMonthYear(YearMonth monthYear);
 	Optional<Expense> findByMonthYearAndId(YearMonth monthYear, Long id);
}