package com.budgetmaster.income.repository;

import com.budgetmaster.config.TestContainersConfig;
import com.budgetmaster.income.model.Income;
import com.budgetmaster.testsupport.income.constants.IncomeConstants;
import com.budgetmaster.testsupport.income.factory.IncomeFactory;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

@Testcontainers
@DataJpaTest
@Import(TestContainersConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class IncomeRepositoryTest {
    // -- Dependencies --
    @Autowired
    private IncomeRepository incomeRepository;
    
    // -- Test Objects --
    private Income testIncome;
    
    @BeforeEach
    void setUp() {
        testIncome = IncomeFactory.createDefaultIncome();
        incomeRepository.deleteAll();
    }

    @Test
    void shouldSaveIncome() {
        Income savedIncome = incomeRepository.save(testIncome);

        assertThat(savedIncome).isNotNull();
        assertThat(savedIncome.getId()).isNotNull();
        assertThat(savedIncome.getName()).isEqualTo(IncomeConstants.Default.NAME);
        assertThat(savedIncome.getSource()).isEqualTo(IncomeConstants.Default.SOURCE);
        assertThat(savedIncome.getMoney().getAmount()).isEqualByComparingTo(IncomeConstants.Default.AMOUNT);
        assertThat(savedIncome.getMoney().getCurrency()).isEqualTo(IncomeConstants.Default.CURRENCY);
        assertThat(savedIncome.getType()).isEqualTo(IncomeConstants.Default.TYPE);
        assertThat(savedIncome.getMonth()).isEqualTo(IncomeConstants.Default.YEAR_MONTH);
        assertThat(savedIncome.getCreatedAt()).isNotNull();
        assertThat(savedIncome.getLastUpdatedAt()).isNotNull();
    }

    @Test
    void shouldFindIncomeById() {
        Income savedIncome = incomeRepository.save(testIncome);
        Income foundIncome = incomeRepository.findById(savedIncome.getId()).orElse(null);
        assertThat(foundIncome).isNotNull();
        assertThat(foundIncome.getId()).isEqualTo(savedIncome.getId());
    }

    @Test
    void shouldFindIncomeByMonth() {
        incomeRepository.save(testIncome);
        List<Income> foundIncomes = incomeRepository.findByMonth(testIncome.getMonth());
        assertThat(foundIncomes).isNotNull();
        assertThat(foundIncomes.size()).isEqualTo(1);
        assertThat(foundIncomes.get(0).getId()).isEqualTo(testIncome.getId());
    }

    @Test
    void shouldReturnEmptyListWhenNoIncomesFound() {
        List<Income> foundIncomes = incomeRepository.findByMonth(testIncome.getMonth());
        assertThat(foundIncomes).isNotNull();
        assertThat(foundIncomes.size()).isEqualTo(0);
    }

    @Test
    @Transactional
    void shouldUpdateIncome() {
        Income savedIncome = incomeRepository.saveAndFlush(testIncome);

        savedIncome.updateFrom(IncomeFactory.createUpdatedIncomeRequest());
        Income updatedIncome = incomeRepository.saveAndFlush(savedIncome);

        assertThat(updatedIncome).isNotNull();
        assertThat(updatedIncome.getId()).isEqualTo(savedIncome.getId());
        assertThat(updatedIncome.getName()).isEqualTo(IncomeConstants.Updated.NAME);
        assertThat(updatedIncome.getSource()).isEqualTo(IncomeConstants.Updated.SOURCE);
        assertThat(updatedIncome.getMoney().getAmount()).isEqualByComparingTo(IncomeConstants.Updated.AMOUNT);
        assertThat(updatedIncome.getMoney().getCurrency()).isEqualTo(IncomeConstants.Default.CURRENCY);
        assertThat(updatedIncome.getType()).isEqualTo(IncomeConstants.Updated.TYPE);
        assertThat(updatedIncome.getMonth()).isEqualTo(IncomeConstants.Updated.YEAR_MONTH);
        assertThat(updatedIncome.getCreatedAt()).isEqualTo(savedIncome.getCreatedAt());
        assertThat(updatedIncome.getLastUpdatedAt()).isAfterOrEqualTo(savedIncome.getLastUpdatedAt());
    }

    @Test
    void shouldDeleteIncome() {
        Income savedIncome = incomeRepository.save(testIncome);
        incomeRepository.delete(savedIncome);
        assertThat(incomeRepository.findById(savedIncome.getId())).isEmpty();
    }
}