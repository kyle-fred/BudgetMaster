package com.budgetmaster.application.repository;

import com.budgetmaster.application.model.Budget;
import com.budgetmaster.integration.config.TestContainersConfig;
import com.budgetmaster.testsupport.assertions.integration.BudgetIntegrationAssertions;
import com.budgetmaster.testsupport.builder.model.BudgetBuilder;
import com.budgetmaster.testsupport.constants.domain.BudgetConstants;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
@DisplayName("Budget Repository Tests")
class BudgetRepositoryTest {

    @Autowired
    private BudgetRepository budgetRepository;

    private Budget savedBudget;

    @BeforeEach
    void setUp() {
        budgetRepository.deleteAll();
        savedBudget = budgetRepository.save(BudgetBuilder.defaultBudget().build());
    }

    @Nested
    @DisplayName("Budget Persistence")
    class BudgetPersistence {
        
        @Test
        @DisplayName("Should save budget to repository")
        void save_withValidBudget_persistsBudget() {
            BudgetIntegrationAssertions.assertBudget(savedBudget)
                .isDefaultBudget();
        }

        @Test
        @DisplayName("Should delete budget from repository")
        void delete_withExistingBudget_removesBudget() {
            budgetRepository.delete(savedBudget);

            BudgetIntegrationAssertions.assertBudgetDeleted(savedBudget, budgetRepository);
        }
    }

    @Nested
    @DisplayName("Budget Retrieval")
    class BudgetRetrieval {
        
        @Test
        @DisplayName("Should find budget by ID")
        void findById_withExistingId_returnsBudget() {
            Budget foundBudget = budgetRepository.findById(savedBudget.getId()).orElse(null);

            BudgetIntegrationAssertions.assertBudget(foundBudget)
                .isEqualTo(savedBudget);
        }

        @Test
        @DisplayName("Should find budget by month")
        void findByMonth_withExistingMonth_returnsBudget() {
            Optional<Budget> foundBudget = budgetRepository.findByMonth(savedBudget.getMonth());
            
            BudgetIntegrationAssertions.assertBudget(foundBudget.get())
                .isEqualTo(savedBudget);
        }

        @Test
        @DisplayName("Should return empty optional when budget not found")
        void findByMonth_withNonExistentMonth_returnsEmptyOptional() {
            Optional<Budget> foundBudget = budgetRepository.findByMonth(BudgetConstants.NonExistent.YEAR_MONTH);

            assertThat(foundBudget).isEmpty();
        }
    }
}
