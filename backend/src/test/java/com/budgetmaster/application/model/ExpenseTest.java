package com.budgetmaster.application.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.budgetmaster.application.dto.ExpenseRequest;
import com.budgetmaster.testsupport.assertions.model.ExpenseModelAssertions;
import com.budgetmaster.testsupport.builder.dto.ExpenseRequestBuilder;
import com.budgetmaster.testsupport.builder.model.ExpenseBuilder;

@DisplayName("Expense Model Tests")
class ExpenseTest {

  private Expense expense;
  private ExpenseRequest defaultExpenseRequest =
      ExpenseRequestBuilder.defaultExpenseRequest().buildRequest();

  @BeforeEach
  void setUp() {
    expense = new Expense();
  }

  @Nested
  @DisplayName("Expense Creation")
  class ExpenseCreation {

    @Test
    @DisplayName("Should create expense from valid request")
    void from_withValidRequest_returnsExpense() {
      expense = Expense.from(defaultExpenseRequest);

      ExpenseModelAssertions.assertExpense(expense).isDefaultExpense();
    }

    @Test
    @DisplayName("Should create expense with valid parameters")
    void of_withValidParameters_createsExpenseWithCorrectValues() {
      expense = ExpenseBuilder.defaultExpense().build();

      ExpenseModelAssertions.assertExpense(expense).isDefaultExpense();
    }
  }

  @Nested
  @DisplayName("Expense Operations")
  class ExpenseOperations {

    @Test
    @DisplayName("Should update expense from valid request")
    void updateFrom_withValidRequest_updatesExpense() {
      expense.updateFrom(defaultExpenseRequest);

      ExpenseModelAssertions.assertExpense(expense).isDefaultExpense();
    }

    @Test
    @DisplayName("Should create deep copy with same values")
    void deepCopy_returnsNewExpenseWithSameValues() {
      expense = ExpenseBuilder.defaultExpense().build();
      Expense copy = expense.deepCopy();

      ExpenseModelAssertions.assertExpense(copy).isDefaultExpense();
    }
  }
}
