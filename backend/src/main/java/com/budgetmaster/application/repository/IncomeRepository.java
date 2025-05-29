package com.budgetmaster.application.repository;

import java.time.YearMonth;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.budgetmaster.application.model.Income;

public interface IncomeRepository extends JpaRepository<Income, Long> {
	List<Income> findByMonth(YearMonth month);
}