package com.budgetmaster.repository;

import com.budgetmaster.model.Income;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IncomeRepository extends JpaRepository<Income, Long> {
	List<Income> findByMonthYear(YearMonth monthYear);
	Optional<Income> findByMonthYearAndId(YearMonth monthYear, Long id);
}