package com.budgetmaster.income.repository;

import com.budgetmaster.config.TestContainersConfig;
import com.budgetmaster.income.model.Income;
import com.budgetmaster.common.enums.TransactionType;
import com.budgetmaster.money.model.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestContainersConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class IncomeRepositoryTest {

    @Autowired
    private IncomeRepository incomeRepository;

    @BeforeEach
    void setUp() {
        incomeRepository.deleteAll();
    }

    @Test
    void shouldSaveIncomeSuccessfully() {
        // Given
        Income income = Income.of(
            "SALARY",
            "EMPLOYER",
            Money.of(new BigDecimal("1000.00"), Currency.getInstance("USD")),
            TransactionType.RECURRING,
            YearMonth.now()
        );

        // When
        Income savedIncome = incomeRepository.save(income);

        // Then
        assertThat(savedIncome).isNotNull();
        assertThat(savedIncome.getId()).isNotNull();
        assertThat(savedIncome.getName()).isEqualTo("SALARY");
        assertThat(savedIncome.getSource()).isEqualTo("EMPLOYER");
        assertThat(savedIncome.getMoney().getAmount()).isEqualTo(new BigDecimal("1000.00"));
        assertThat(savedIncome.getMoney().getCurrency()).isEqualTo(Currency.getInstance("USD"));
        assertThat(savedIncome.getType()).isEqualTo(TransactionType.RECURRING);
        assertThat(savedIncome.getMonth()).isEqualTo(YearMonth.now());
    }
} 