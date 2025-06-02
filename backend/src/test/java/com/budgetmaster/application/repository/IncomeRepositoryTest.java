package com.budgetmaster.application.repository;

import com.budgetmaster.application.model.Income;
import com.budgetmaster.integration.config.TestContainersConfig;
import com.budgetmaster.testsupport.assertions.integration.IncomeIntegrationAssertions;
import com.budgetmaster.testsupport.assertions.integration.list.IncomeIntegrationListAssertions;
import com.budgetmaster.testsupport.builder.model.IncomeBuilder;
import com.budgetmaster.testsupport.constants.domain.IncomeConstants;

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
public class IncomeRepositoryTest {

    @Autowired
    private IncomeRepository incomeRepository;

    private Income savedIncome;
    
    @BeforeEach
    void setUp() {
        incomeRepository.deleteAll();
        savedIncome = incomeRepository.save(IncomeBuilder.defaultIncome().build());
    }

    @Test
    void shouldSaveIncome() {
        IncomeIntegrationAssertions.assertIncome(savedIncome)
            .isDefaultIncome();
    }

    @Test
    void shouldFindIncomeById() {
        Income foundIncome = incomeRepository.findById(savedIncome.getId()).orElse(null);

        IncomeIntegrationAssertions.assertIncome(foundIncome)
            .isEqualTo(savedIncome);
    }

    @Test
    void shouldFindIncomeByMonth() {
        List<Income> foundIncomes = incomeRepository.findByMonth(savedIncome.getMonth());

        IncomeIntegrationListAssertions.assertIncomes(foundIncomes)
            .hasSize(1)
            .first()
            .isEqualTo(savedIncome);
    }

    @Test
    void shouldReturnEmptyListWhenNoIncomesFound() {
        List<Income> foundIncomes = incomeRepository.findByMonth(IncomeConstants.NonExistent.YEAR_MONTH);

        IncomeIntegrationListAssertions.assertIncomes(foundIncomes)
            .hasSize(0);
    }

    @Test
    void shouldDeleteIncome() {
        incomeRepository.delete(savedIncome);

        IncomeIntegrationAssertions.assertIncomeDeleted(savedIncome, incomeRepository);
    }
}