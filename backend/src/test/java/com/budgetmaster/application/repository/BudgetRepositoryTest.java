package com.budgetmaster.application.repository;

import com.budgetmaster.application.model.Budget;
import com.budgetmaster.integration.config.TestContainersConfig;
import com.budgetmaster.testsupport.assertions.integration.BudgetIntegrationAssertions;
import com.budgetmaster.testsupport.builder.model.BudgetBuilder;
import com.budgetmaster.testsupport.constants.domain.BudgetConstants;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

@Testcontainers
@DataJpaTest
@Import(TestContainersConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class BudgetRepositoryTest {

    @Autowired
    private BudgetRepository budgetRepository;

    private Budget testBudget;

    @BeforeEach
    void setUp() {
        budgetRepository.deleteAll();
        testBudget = budgetRepository.save(BudgetBuilder.defaultBudget().build());
    }

    @Test
    void shouldSaveBudget() {
        BudgetIntegrationAssertions.assertBudget(testBudget)
            .isDefaultBudget();
    }

    @Test
    void shouldFindBudgetById() {
        Budget foundBudget = budgetRepository.findById(testBudget.getId()).orElse(null);

        BudgetIntegrationAssertions.assertBudget(foundBudget)
            .isEqualTo(testBudget);
    }

    @Test
    void shouldFindBudgetByMonth() {
        Optional<Budget> foundBudget = budgetRepository.findByMonth(testBudget.getMonth());
        
        BudgetIntegrationAssertions.assertBudget(foundBudget.get())
            .isEqualTo(testBudget);
    }

    @Test
    void shouldReturnEmptyOptionalWhenNoBudgetFound() {
        Optional<Budget> foundBudget = budgetRepository.findByMonth(BudgetConstants.NonExistent.YEAR_MONTH);

        assertThat(foundBudget).isEmpty();
    }

    @Test
    void shouldDeleteBudget() {
        budgetRepository.delete(testBudget);

        BudgetIntegrationAssertions.assertBudgetDeleted(testBudget, budgetRepository);
    }
}
