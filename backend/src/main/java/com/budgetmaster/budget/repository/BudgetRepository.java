package com.budgetmaster.budget.repository;

import java.time.YearMonth;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.budgetmaster.budget.model.Budget;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
	Optional<Budget> findByMonth(YearMonth month);
}