package com.budgetmaster.application.repository;

import com.budgetmaster.application.model.Expense;
import com.budgetmaster.integration.config.TestContainersConfig;
import com.budgetmaster.testsupport.assertions.integration.ExpenseIntegrationAssertions;
import com.budgetmaster.testsupport.assertions.integration.list.ExpenseIntegrationListAssertions;
import com.budgetmaster.testsupport.builder.model.ExpenseBuilder;
import com.budgetmaster.testsupport.constants.domain.ExpenseConstants;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

@Testcontainers
@DataJpaTest
@Import(TestContainersConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ExpenseRepositoryTest {

   @Autowired
   private ExpenseRepository expenseRepository;

   private Expense savedExpense;

   @BeforeEach
   void setUp() {
       expenseRepository.deleteAll();
       savedExpense = expenseRepository.save(ExpenseBuilder.defaultExpense().build());
   }

   @Test
   void shouldSaveExpense() {
       ExpenseIntegrationAssertions.assertExpense(savedExpense)
            .isDefaultExpense();
   }

   @Test
   void shouldFindExpenseById() {
       Expense foundExpense = expenseRepository.findById(savedExpense.getId()).orElse(null);

       ExpenseIntegrationAssertions.assertExpense(foundExpense)
            .isEqualTo(savedExpense);
   }

   @Test
   void shouldFindExpenseByMonth() {
       List<Expense> foundExpenses = expenseRepository.findByMonth(savedExpense.getMonth());

       ExpenseIntegrationListAssertions.assertExpenses(foundExpenses)
            .hasSize(1)
            .contains(savedExpense);
   }

   @Test
   void shouldReturnEmptyListWhenNoExpensesFound() {
       List<Expense> foundExpenses = expenseRepository.findByMonth(ExpenseConstants.NonExistent.YEAR_MONTH);

       ExpenseIntegrationListAssertions.assertExpenses(foundExpenses)
            .hasSize(0);
   }

   @Test
   void shouldDeleteExpense() {
       expenseRepository.delete(savedExpense);

       ExpenseIntegrationAssertions.assertExpenseDeleted(savedExpense, expenseRepository);
   }
}
