package com.budgetmaster.budget.repository;

import com.budgetmaster.config.TestContainersConfig;
import com.budgetmaster.budget.model.Budget;
import com.budgetmaster.testsupport.budget.builder.BudgetBuilder;
import com.budgetmaster.testsupport.budget.constants.BudgetConstants;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

@Testcontainers
@DataJpaTest
@Import(TestContainersConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BudgetRepositoryTest {
        // -- Dependencies --
        @Autowired
        private BudgetRepository budgetRepository;
    
        // -- Test Objects --
        private Budget testBudget;
    
        @BeforeEach
        void setUp() {
            testBudget = BudgetBuilder.defaultBudget()
                .withId(null)
                .build();
            budgetRepository.deleteAll();
        }
    
        @Test
        void shouldSaveBudget() {
            Budget savedBudget = budgetRepository.save(testBudget);
    
            assertThat(savedBudget).isNotNull();
            assertThat(savedBudget.getId()).isNotNull();
            assertThat(savedBudget.getTotalIncome()).isEqualTo(BudgetConstants.Default.TOTAL_INCOME);
            assertThat(savedBudget.getTotalExpense()).isEqualTo(BudgetConstants.Default.TOTAL_EXPENSE);
            assertThat(savedBudget.getSavings()).isEqualTo(BudgetConstants.Default.SAVINGS);
            assertThat(savedBudget.getCurrency()).isEqualTo(BudgetConstants.Default.CURRENCY);
            assertThat(savedBudget.getMonth()).isEqualTo(BudgetConstants.Default.YEAR_MONTH);
            assertThat(savedBudget.getCreatedAt()).isNotNull();
            assertThat(savedBudget.getLastUpdatedAt()).isNotNull();
        }
    
        @Test
        void shouldFindBudgetById() {
            Budget savedBudget = budgetRepository.save(testBudget);
            Budget foundBudget = budgetRepository.findById(savedBudget.getId()).orElse(null);
            assertThat(foundBudget).isNotNull();
            assertThat(foundBudget.getId()).isEqualTo(savedBudget.getId());
        }
    
        @Test
        void shouldFindBudgetByMonth() {
            Budget savedBudget = budgetRepository.save(testBudget);
            Optional<Budget> foundBudget = budgetRepository.findByMonth(testBudget.getMonth());
            assertThat(foundBudget).isNotNull();
            assertThat(foundBudget.get().getId()).isEqualTo(savedBudget.getId());
        }
    
        @Test
        void shouldReturnEmptyOptionalWhenNoBudgetFound() {
            Optional<Budget> foundBudget = budgetRepository.findByMonth(testBudget.getMonth());
            assertThat(foundBudget).isNotNull();
            assertThat(foundBudget.isEmpty()).isTrue();
        }
    
        @Test
        void shouldDeleteBudget() {
            Budget savedBudget = budgetRepository.save(testBudget);
            budgetRepository.delete(savedBudget);
            assertThat(budgetRepository.findById(savedBudget.getId())).isEmpty();
        }
}
