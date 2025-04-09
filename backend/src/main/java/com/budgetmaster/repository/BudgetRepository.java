package com.budgetmaster.repository;

import com.budgetmaster.model.Budget;

import java.time.YearMonth;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
	Optional<Budget> findByMonthYear(YearMonth monthYear);
}