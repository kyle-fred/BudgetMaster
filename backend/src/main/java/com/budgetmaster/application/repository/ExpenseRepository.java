package com.budgetmaster.application.repository;

import java.time.YearMonth;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.budgetmaster.application.model.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
  List<Expense> findByMonth(YearMonth month);
}
