package com.budgetmaster.income.model;

import com.budgetmaster.income.dto.IncomeRequest;
import com.budgetmaster.test.constants.TestData.IncomeTestConstants;
import com.budgetmaster.test.factory.IncomeTestFactory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IncomeTest {
    
    @Test
    void from_ValidRequest_ReturnsIncome() {
        IncomeRequest incomeRequest = IncomeTestFactory.createDefaultIncomeRequest();
        Income income = Income.from(incomeRequest);
        
        assertNotNull(income);
        assertEquals(IncomeTestConstants.Default.NAME, income.getName());
        assertEquals(IncomeTestConstants.Default.SOURCE, income.getSource());
        assertEquals(IncomeTestConstants.Default.AMOUNT, income.getMoney().getAmount());
        assertEquals(IncomeTestConstants.Default.CURRENCY, income.getMoney().getCurrency());
        assertEquals(IncomeTestConstants.Default.TYPE, income.getType());
        assertEquals(IncomeTestConstants.Default.YEAR_MONTH, income.getMonth());
    }
    
    @Test
    void updateFrom_ValidRequest_UpdatesIncome() {
        Income income = new Income();
        IncomeRequest incomeRequest = IncomeTestFactory.createDefaultIncomeRequest();
        income.updateFrom(incomeRequest);
        
        assertEquals(IncomeTestConstants.Default.NAME, income.getName());
        assertEquals(IncomeTestConstants.Default.SOURCE, income.getSource());
        assertEquals(IncomeTestConstants.Default.AMOUNT, income.getMoney().getAmount());
        assertEquals(IncomeTestConstants.Default.CURRENCY, income.getMoney().getCurrency());
        assertEquals(IncomeTestConstants.Default.TYPE, income.getType());
        assertEquals(IncomeTestConstants.Default.YEAR_MONTH, income.getMonth());
    }

    @Test
    void deepCopy_ReturnsNewIncomeWithSameValues() {
        Income income = IncomeTestFactory.createDefaultIncome();
        Income copy = income.deepCopy();

        assertNotSame(income, copy);
        assertEquals(income.getName(), copy.getName());
        assertEquals(income.getSource(), copy.getSource());
        assertEquals(income.getMoney().getAmount(), copy.getMoney().getAmount());
        assertEquals(income.getMoney().getCurrency(), copy.getMoney().getCurrency());
        assertEquals(income.getType(), copy.getType());
        assertEquals(income.getMonth(), copy.getMonth());
    }

    @Test
    void of_WithValidParameters_CreatesIncomeWithCorrectValues() {
        Income income = IncomeTestFactory.createDefaultIncome();

        assertNotNull(income);
        assertEquals(IncomeTestConstants.Default.NAME, income.getName());
        assertEquals(IncomeTestConstants.Default.SOURCE, income.getSource());
        assertEquals(IncomeTestConstants.Default.AMOUNT, income.getMoney().getAmount());
        assertEquals(IncomeTestConstants.Default.CURRENCY, income.getMoney().getCurrency());
        assertEquals(IncomeTestConstants.Default.TYPE, income.getType());
        assertEquals(IncomeTestConstants.Default.YEAR_MONTH, income.getMonth());
    }
}
