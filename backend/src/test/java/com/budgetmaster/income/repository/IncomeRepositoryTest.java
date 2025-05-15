package com.budgetmaster.income.repository;

import com.budgetmaster.common.enums.TransactionType;
import com.budgetmaster.income.model.Income;
import com.budgetmaster.money.model.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class IncomeRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private IncomeRepository incomeRepository;

    @Test
    void connectionEstablished() {
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }

    @Test
    void shouldSaveAndRetrieveIncome() {
        // Given
        Income income = Income.of(
            "Test Income",
            "Test Source",
            Money.of(new BigDecimal("1000.00"), Currency.getInstance("USD")),
            TransactionType.RECURRING,
            YearMonth.of(2024, 5)
        );

        // When
        Income savedIncome = incomeRepository.save(income);
        Income retrievedIncome = incomeRepository.findById(savedIncome.getId()).orElse(null);

        // Then
        assertThat(retrievedIncome).isNotNull();
        assertThat(retrievedIncome.getName()).isEqualTo("Test Income");
        assertThat(retrievedIncome.getSource()).isEqualTo("Test Source");
        assertThat(retrievedIncome.getMoney().getAmount()).isEqualByComparingTo(new BigDecimal("1000.00"));
        assertThat(retrievedIncome.getMoney().getCurrency()).isEqualTo(Currency.getInstance("USD"));
        assertThat(retrievedIncome.getType()).isEqualTo(TransactionType.RECURRING);
        assertThat(retrievedIncome.getMonth()).isEqualTo(YearMonth.of(2024, 5));
    }
} 