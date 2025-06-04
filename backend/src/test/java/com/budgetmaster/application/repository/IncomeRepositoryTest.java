package com.budgetmaster.application.repository;

import com.budgetmaster.application.model.Income;
import com.budgetmaster.integration.config.TestContainersConfig;
import com.budgetmaster.testsupport.assertions.integration.IncomeIntegrationAssertions;
import com.budgetmaster.testsupport.assertions.integration.list.IncomeIntegrationListAssertions;
import com.budgetmaster.testsupport.builder.model.IncomeBuilder;
import com.budgetmaster.testsupport.constants.domain.IncomeConstants;

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

import java.util.List;

@Testcontainers
@DataJpaTest
@Import(TestContainersConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@DisplayName("Income Repository Tests")
class IncomeRepositoryTest {

    @Autowired
    private IncomeRepository incomeRepository;

    private Income savedIncome;
    
    @BeforeEach
    void setUp() {
        incomeRepository.deleteAll();
        savedIncome = incomeRepository.save(IncomeBuilder.defaultIncome().build());
    }

    @Nested
    @DisplayName("Income Persistence")
    class IncomePersistence {
        
        @Test
        @DisplayName("Should save income to repository")
        void save_withValidIncome_persistsIncome() {
            IncomeIntegrationAssertions.assertIncome(savedIncome)
                .isDefaultIncome();
        }

        @Test
        @DisplayName("Should delete income from repository")
        void delete_withExistingIncome_removesIncome() {
            incomeRepository.delete(savedIncome);

            IncomeIntegrationAssertions.assertIncomeDeleted(savedIncome, incomeRepository);
        }
    }

    @Nested
    @DisplayName("Income Retrieval")
    class IncomeRetrieval {
        
        @Test
        @DisplayName("Should find income by ID")
        void findById_withExistingId_returnsIncome() {
            Income foundIncome = incomeRepository.findById(savedIncome.getId()).orElse(null);

            IncomeIntegrationAssertions.assertIncome(foundIncome)
                .isEqualTo(savedIncome);
        }

        @Test
        @DisplayName("Should find incomes by month")
        void findByMonth_withExistingMonth_returnsIncomes() {
            List<Income> foundIncomes = incomeRepository.findByMonth(savedIncome.getMonth());

            IncomeIntegrationListAssertions.assertIncomes(foundIncomes)
                .hasSize(1)
                .contains(savedIncome);
        }

        @Test
        @DisplayName("Should return empty list when no incomes found")
        void findByMonth_withNonExistentMonth_returnsEmptyList() {
            List<Income> foundIncomes = incomeRepository.findByMonth(IncomeConstants.NonExistent.YEAR_MONTH);

            IncomeIntegrationListAssertions.assertIncomes(foundIncomes)
                .hasSize(0);
        }
    }
}