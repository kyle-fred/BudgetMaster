package com.budgetmaster.application.service;

import java.time.YearMonth;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.budgetmaster.application.exception.BudgetNotFoundException;
import com.budgetmaster.application.model.Budget;
import com.budgetmaster.application.repository.BudgetRepository;
import com.budgetmaster.application.service.core.EntityLookupService;
import com.budgetmaster.application.util.DateUtils;
import com.budgetmaster.constants.error.ErrorMessages;

@Service
public class BudgetService extends EntityLookupService {

  private final BudgetRepository budgetRepository;

  public BudgetService(BudgetRepository budgetRepository) {
    this.budgetRepository = budgetRepository;
  }

  public Budget getBudgetByMonth(String monthString) {
    YearMonth month = DateUtils.getValidYearMonth(monthString);
    return findByCustomFinderOrThrow(
        budgetRepository::findByMonth, month, createMonthNotFoundException(month));
  }

  public Budget getBudgetById(Long id) {
    return findByIdOrThrow(budgetRepository, id, createIdNotFoundException(id));
  }

  @Transactional
  public void deleteBudget(Long id) {
    // TODO: Fix cascade delete - orphaned income/expense records remain when budget is deleted causing data inconsistency on recreation
    getBudgetById(id);
    budgetRepository.deleteById(id);
  }

  /** Creates a supplier for BudgetNotFoundException when entity is not found by ID. */
  private Supplier<BudgetNotFoundException> createIdNotFoundException(Long id) {
    return () ->
        new BudgetNotFoundException(String.format(ErrorMessages.Budget.NOT_FOUND_WITH_ID, id));
  }

  /** Creates a supplier for BudgetNotFoundException when entity is not found by month. */
  private Supplier<BudgetNotFoundException> createMonthNotFoundException(YearMonth month) {
    return () ->
        new BudgetNotFoundException(String.format(ErrorMessages.Budget.NOT_FOUND_FOR_MONTH, month));
  }
}
