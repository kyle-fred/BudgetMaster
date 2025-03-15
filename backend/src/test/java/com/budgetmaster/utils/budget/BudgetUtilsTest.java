package com.budgetmaster.utils.budget;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.YearMonth;

import org.junit.jupiter.api.Test;

import com.budgetmaster.dto.BudgetRequest;
import com.budgetmaster.exception.InvalidMonthYearExceptionHandler;
import com.budgetmaster.model.Budget;

public class BudgetUtilsTest {
	
	@Test
	void testGetValidYearMonth_NullInput_ReturnsCurrentYearMonth() {
		YearMonth expected = YearMonth.now();
		YearMonth actual = BudgetUtils.getValidYearMonth(null);
		assertNotNull(actual);
		assertEquals(expected, actual, "Should return current YearMonth when input is null");
	}
	
	@Test
	void testGetValidYearMonth_EmptyInput_ReturnsCurrentYearMonth() {
		YearMonth expected = YearMonth.now();
		YearMonth actual = BudgetUtils.getValidYearMonth("");
		assertNotNull(actual);
		assertEquals(expected, actual, "Should return current YearMonth when input is empty");
	}
	
	@Test
	void testGetValidYearMonth_ValidInput_ReturnsParsedYearMonth() {
		YearMonth expected = YearMonth.of(2025, 3);
		YearMonth actual = BudgetUtils.getValidYearMonth("2025-03");
		assertNotNull(actual);
		assertEquals(expected, actual, "Should correctly parse and return the set YearMonth");
	}
	
	@Test
	void testGetValidYearMonth_InvalidInput_ThrowsException() {
		assertThrows(InvalidMonthYearExceptionHandler.class, () -> {
			BudgetUtils.getValidYearMonth("2024/04");
		}, "Should throw InvalidMonthYearExceptionHandler for invalid month.");
	}
	
	@Test
	void testGetValidYearMonth_InvalidMonth_ThrowsException() {
		assertThrows(InvalidMonthYearExceptionHandler.class, () -> {
			BudgetUtils.getValidYearMonth("2024-13");
		}, "Should throw InvalidMonthYearExceptionHandler for invalid month.");
	}
	
	@Test
	void testParseYearMonth_ValidInput_ReturnsParsedYearMonth() {
		YearMonth expected = YearMonth.of(2025, 3);
		YearMonth actual = BudgetUtils.getValidYearMonth("2025-03");
		assertEquals(expected, actual, "Should correctly parse the valid YearMonth string");
	}
	
	@Test
	void testParseYearMonth_InvalidFormat_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            BudgetUtils.parseYearMonth("11-2023");
        }, "Should throw IllegalArgumentException for invalid format");
	}
	
    @Test
    void testParseYearMonth_InvalidCharacters_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            BudgetUtils.parseYearMonth("abcd-ef");
        }, "Should throw IllegalArgumentException for non-numeric input");
    }
    
    @Test
    void testBuildBudgetRequest_SetsValidMonthYear() {
        BudgetRequest request = new BudgetRequest();
        request.setMonthYear("2022-08");
        
        BudgetRequest result = BudgetUtils.buildBudgetRequest(request);
        
        assertEquals("2022-08", result.getMonthYear(), "Should correctly set the monthYear");
    }

    @Test
    void testBuildBudget_CreatesBudgetWithValidValues() {
        BudgetRequest request = new BudgetRequest();
        request.setIncome(5000D);
        request.setExpenses(2000D);
        request.setMonthYear("2000-01");
        
        Budget budget = BudgetUtils.buildBudget(request);
        
        assertNotNull(budget, "Budget object should not be null");
        assertEquals(5000, budget.getIncome(), "Income should match");
        assertEquals(2000, budget.getExpenses(), "Expenses should match");
        assertEquals(YearMonth.of(2000, 1), budget.getMonthYear(), "MonthYear should be correctly set");
    }
    
    @Test
    void testModifyBudget_ValidRequest_ModifiesBudgetCorrectly() {
        
    	Budget budget = new Budget(3000.0, 1500.0, YearMonth.of(2000, 1));
        BudgetRequest request = new BudgetRequest();
        request.setIncome(4000.0);
        request.setExpenses(2000.0);
        request.setMonthYear("2020-01");

        BudgetUtils.modifyBudget(budget, request);

        assertEquals(4000.0, budget.getIncome(), "Income should be updated");
        assertEquals(2000.0, budget.getExpenses(), "Expenses should be updated");
        assertEquals(2000.0, budget.getSavings(), "Savings should be recalculated");
        assertEquals(YearMonth.of(2020, 1), budget.getMonthYear(), "MonthYear should be updated");
    }

    @Test
    void testModifyBudget_NullMonthYear_DoesNotChangeMonthYear() {
    	
        YearMonth originalMonthYear = YearMonth.of(2000, 1);
        
        Budget budget = new Budget(3000.0, 1500.0, originalMonthYear);
        BudgetRequest request = new BudgetRequest();
        request.setIncome(4000.0);
        request.setExpenses(2000.0);
        request.setMonthYear(null);

        BudgetUtils.modifyBudget(budget, request);

        assertEquals(originalMonthYear, budget.getMonthYear(), "MonthYear should remain unchanged");
    }

    @Test
    void testModifyBudget_EmptyMonthYear_DoesNotChangeMonthYear() {
        
    	YearMonth originalMonthYear = YearMonth.of(2023, 5);
        Budget budget = new Budget(3000.0, 1500.0, originalMonthYear);
        BudgetRequest request = new BudgetRequest();
        request.setIncome(4000.0);
        request.setExpenses(2000.0);
        request.setMonthYear("");

        BudgetUtils.modifyBudget(budget, request);

        assertEquals(originalMonthYear, budget.getMonthYear(), "MonthYear should remain unchanged when empty");
    }
}