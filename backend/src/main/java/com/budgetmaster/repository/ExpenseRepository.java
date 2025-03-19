package com.budgetmaster.repository;

import com.budgetmaster.model.Expense;

import java.time.YearMonth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
	
	@Query("SELECT COALESCE (SUM(e.AMOUNT), 0) FROM EXPENSE e WHERE e.monthYear = :monthYear")
	Double sumByMonthYear(@Param("monthYear") YearMonth monthYear);
}