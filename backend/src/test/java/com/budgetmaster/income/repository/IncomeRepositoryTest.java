package com.budgetmaster.income.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.budgetmaster.income.model.Income;
import com.budgetmaster.testsupport.income.builder.IncomeBuilder;
import com.budgetmaster.testsupport.income.constants.IncomeConstants;

@DataJpaTest
@ActiveProfiles("test")
class IncomeRepositoryTest {

    @Autowired
    private IncomeRepository incomeRepository;

    private Income defaultIncome;
    private YearMonth testMonth;

    @BeforeEach
    void setUp() {
        // Clear the database before each test
        incomeRepository.deleteAll();
        
        // Create a default income for testing
        defaultIncome = IncomeBuilder.defaultIncome().build();
        testMonth = IncomeConstants.Default.YEAR_MONTH;
    }

    @Nested
    @DisplayName("Save Operations")
    class SaveOperations {
        
        @Test
        @DisplayName("Should save income successfully")
        void shouldSaveIncomeSuccessfully() {
            // When
            Income savedIncome = incomeRepository.save(defaultIncome);

            // Then
            assertThat(savedIncome).isNotNull();
            assertThat(savedIncome.getId()).isNotNull();
            assertThat(savedIncome.getName()).isEqualTo(defaultIncome.getName());
            assertThat(savedIncome.getSource()).isEqualTo(defaultIncome.getSource());
            assertThat(savedIncome.getMoney()).isEqualTo(defaultIncome.getMoney());
            assertThat(savedIncome.getType()).isEqualTo(defaultIncome.getType());
            assertThat(savedIncome.getMonth()).isEqualTo(defaultIncome.getMonth());
        }

        @Test
        @DisplayName("Should update existing income")
        void shouldUpdateExistingIncome() {
            // Given
            Income savedIncome = incomeRepository.save(defaultIncome);
            String updatedName = "UPDATED SALARY";
            savedIncome.setName(updatedName);

            // When
            Income updatedIncome = incomeRepository.save(savedIncome);

            // Then
            assertThat(updatedIncome.getId()).isEqualTo(savedIncome.getId());
            assertThat(updatedIncome.getName()).isEqualTo(updatedName);
        }
    }

    @Nested
    @DisplayName("Find Operations")
    class FindOperations {

        @Test
        @DisplayName("Should find income by month")
        void shouldFindIncomeByMonth() {
            // Given
            Income income1 = IncomeBuilder.defaultIncome().build();
            Income income2 = IncomeBuilder.defaultIncome()
                .withName("BONUS")
                .build();
            incomeRepository.save(income1);
            incomeRepository.save(income2);

            // When
            List<Income> foundIncomes = incomeRepository.findByMonth(testMonth);

            // Then
            assertThat(foundIncomes).hasSize(2);
            assertThat(foundIncomes).allMatch(income -> 
                income.getMonth().equals(testMonth));
        }

        @Test
        @DisplayName("Should return empty list when no incomes found for month")
        void shouldReturnEmptyListWhenNoIncomesFoundForMonth() {
            // Given
            YearMonth differentMonth = YearMonth.of(2024, 2);

            // When
            List<Income> foundIncomes = incomeRepository.findByMonth(differentMonth);

            // Then
            assertThat(foundIncomes).isEmpty();
        }

        @Test
        @DisplayName("Should find income by id")
        void shouldFindIncomeById() {
            // Given
            Income savedIncome = incomeRepository.save(defaultIncome);

            // When
            Optional<Income> foundIncome = incomeRepository.findById(savedIncome.getId());

            // Then
            assertThat(foundIncome).isPresent();
            assertThat(foundIncome.get().getId()).isEqualTo(savedIncome.getId());
        }

        @Test
        @DisplayName("Should return empty when income not found by id")
        void shouldReturnEmptyWhenIncomeNotFoundById() {
            // When
            Optional<Income> foundIncome = incomeRepository.findById(999L);

            // Then
            assertThat(foundIncome).isEmpty();
        }
    }

    @Nested
    @DisplayName("Delete Operations")
    class DeleteOperations {

        @Test
        @DisplayName("Should delete income successfully")
        void shouldDeleteIncomeSuccessfully() {
            // Given
            Income savedIncome = incomeRepository.save(defaultIncome);

            // When
            incomeRepository.delete(savedIncome);

            // Then
            assertThat(incomeRepository.findById(savedIncome.getId())).isEmpty();
        }

        @Test
        @DisplayName("Should delete all incomes")
        void shouldDeleteAllIncomes() {
            // Given
            Income income1 = IncomeBuilder.defaultIncome().build();
            Income income2 = IncomeBuilder.defaultIncome()
                .withName("BONUS")
                .build();
            incomeRepository.save(income1);
            incomeRepository.save(income2);

            // When
            incomeRepository.deleteAll();

            // Then
            assertThat(incomeRepository.findAll()).isEmpty();
        }
    }
} 