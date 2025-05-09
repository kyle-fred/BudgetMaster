package com.budgetmaster.income.model;

import com.budgetmaster.income.dto.IncomeRequest;
import com.budgetmaster.testsupport.income.constants.IncomeConstants;
import com.budgetmaster.testsupport.income.factory.IncomeFactory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IncomeTest {
    
    @Test
    void from_ValidRequest_ReturnsIncome() {
        IncomeRequest incomeRequest = IncomeFactory.createDefaultIncomeRequest();
        Income income = Income.from(incomeRequest);
        
        assertNotNull(income);
        assertEquals(IncomeConstants.Default.NAME, income.getName());
        assertEquals(IncomeConstants.Default.SOURCE, income.getSource());
        assertEquals(IncomeConstants.Default.AMOUNT, income.getMoney().getAmount());
        assertEquals(IncomeConstants.Default.CURRENCY, income.getMoney().getCurrency());
        assertEquals(IncomeConstants.Default.TYPE, income.getType());
        assertEquals(IncomeConstants.Default.YEAR_MONTH, income.getMonth());
    }
    
    @Test
    void updateFrom_ValidRequest_UpdatesIncome() {
        Income income = new Income();
        IncomeRequest incomeRequest = IncomeFactory.createDefaultIncomeRequest();
        income.updateFrom(incomeRequest);
        
        assertEquals(IncomeConstants.Default.NAME, income.getName());
        assertEquals(IncomeConstants.Default.SOURCE, income.getSource());
        assertEquals(IncomeConstants.Default.AMOUNT, income.getMoney().getAmount());
        assertEquals(IncomeConstants.Default.CURRENCY, income.getMoney().getCurrency());
        assertEquals(IncomeConstants.Default.TYPE, income.getType());
        assertEquals(IncomeConstants.Default.YEAR_MONTH, income.getMonth());
    }

    @Test
    void deepCopy_ReturnsNewIncomeWithSameValues() {
        Income income = IncomeFactory.createDefaultIncome();
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
        Income income = IncomeFactory.createDefaultIncome();

        assertNotNull(income);
        assertEquals(IncomeConstants.Default.NAME, income.getName());
        assertEquals(IncomeConstants.Default.SOURCE, income.getSource());
        assertEquals(IncomeConstants.Default.AMOUNT, income.getMoney().getAmount());
        assertEquals(IncomeConstants.Default.CURRENCY, income.getMoney().getCurrency());
        assertEquals(IncomeConstants.Default.TYPE, income.getType());
        assertEquals(IncomeConstants.Default.YEAR_MONTH, income.getMonth());
    }
}
