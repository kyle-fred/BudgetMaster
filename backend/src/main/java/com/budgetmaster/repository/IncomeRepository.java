package com.budgetmaster.repository;

import com.budgetmaster.model.Income;

import java.time.YearMonth;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IncomeRepository extends JpaRepository<Income, Long> {
	List<Income> findByMonthYear(YearMonth monthYear);
}