package com.budgetmaster.expense.repository;

import com.budgetmaster.application.model.Expense;
import com.budgetmaster.config.TestContainersConfig;
import com.budgetmaster.testsupport.expense.constants.ExpenseConstants;
import com.budgetmaster.testsupport.expense.factory.ExpenseFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

@Testcontainers
@DataJpaTest
@Import(TestContainersConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ExpenseRepositoryTest {
   // -- Dependencies --
   @Autowired
   private ExpenseRepository expenseRepository;

   // -- Test Objects --
   private Expense testExpense;

   @BeforeEach
   void setUp() {
       testExpense = ExpenseFactory.createDefaultExpense();
       expenseRepository.deleteAll();
   }

   @Test
   void shouldSaveExpense() {
       Expense savedExpense = expenseRepository.save(testExpense);

       assertThat(savedExpense).isNotNull();
       assertThat(savedExpense.getId()).isNotNull();
       assertThat(savedExpense.getName()).isEqualTo(ExpenseConstants.Default.NAME);
       assertThat(savedExpense.getCategory()).isEqualTo(ExpenseConstants.Default.CATEGORY);
       assertThat(savedExpense.getMoney().getAmount()).isEqualByComparingTo(ExpenseConstants.Default.AMOUNT);
       assertThat(savedExpense.getMoney().getCurrency()).isEqualTo(ExpenseConstants.Default.CURRENCY);
       assertThat(savedExpense.getType()).isEqualTo(ExpenseConstants.Default.TYPE);
       assertThat(savedExpense.getMonth()).isEqualTo(ExpenseConstants.Default.YEAR_MONTH);
       assertThat(savedExpense.getCreatedAt()).isNotNull();
       assertThat(savedExpense.getLastUpdatedAt()).isNotNull();
   }

   @Test
   void shouldFindExpenseById() {
       Expense savedExpense = expenseRepository.save(testExpense);
       Expense foundExpense = expenseRepository.findById(savedExpense.getId()).orElse(null);
       assertThat(foundExpense).isNotNull();
       assertThat(foundExpense.getId()).isEqualTo(savedExpense.getId());
   }

   @Test
   void shouldFindExpenseByMonth() {
       expenseRepository.save(testExpense);
       List<Expense> foundExpenses = expenseRepository.findByMonth(testExpense.getMonth());
       assertThat(foundExpenses).isNotNull();
       assertThat(foundExpenses.size()).isEqualTo(1);
       assertThat(foundExpenses.get(0).getId()).isEqualTo(testExpense.getId());
   }

   @Test
   void shouldReturnEmptyListWhenNoExpensesFound() {
       List<Expense> foundExpenses = expenseRepository.findByMonth(testExpense.getMonth());
       assertThat(foundExpenses).isNotNull();
       assertThat(foundExpenses.size()).isEqualTo(0);
   }

   @Test
   void shouldDeleteExpense() {
       Expense savedExpense = expenseRepository.save(testExpense);
       expenseRepository.delete(savedExpense);
       assertThat(expenseRepository.findById(savedExpense.getId())).isEmpty();
   }
}
