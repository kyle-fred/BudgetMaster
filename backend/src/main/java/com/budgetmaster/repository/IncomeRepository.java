package com.budgetmaster.repository;

import com.budgetmaster.model.Income;

import java.time.YearMonth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {
	
	@Query("SELECT COALESCE (SUM(i.AMOUNT), 0) FROM INCOME i WHERE i.monthYear = :monthYear")
	Double sumByMonthYear(@Param("monthYear") YearMonth monthYear);
}