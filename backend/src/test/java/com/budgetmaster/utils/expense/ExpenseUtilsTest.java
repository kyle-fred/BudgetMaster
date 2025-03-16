package com.budgetmaster.utils.expense;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.YearMonth;

import org.junit.jupiter.api.Test;

import com.budgetmaster.dto.ExpenseRequest;
import com.budgetmaster.enums.ExpenseCategory;
import com.budgetmaster.enums.TransactionType;
import com.budgetmaster.exception.InvalidMonthYearExceptionHandler;
import com.budgetmaster.model.Expense;

public class ExpenseUtilsTest {
	
	@Test
	void testGetValidYearMonth_NullInput_ReturnsCurrentYearMonth() {
		YearMonth expected = YearMonth.now();
		YearMonth actual = ExpenseUtils.getValidYearMonth(null);
		assertNotNull(actual);
		assertEquals(expected, actual, "Should return current YearMonth when input is null");
	}
	
	@Test
	void testGetValidYearMonth_EmptyInput_ReturnsCurrentYearMonth() {
		YearMonth expected = YearMonth.now();
		YearMonth actual = ExpenseUtils.getValidYearMonth("");
		assertNotNull(actual);
		assertEquals(expected, actual, "Should return current YearMonth when input is empty");
	}
	
	@Test
	void testGetValidYearMonth_ValidInput_ReturnsParsedYearMonth() {
		YearMonth expected = YearMonth.of(2025, 3);
		YearMonth actual = ExpenseUtils.getValidYearMonth("2025-03");
		assertNotNull(actual);
		assertEquals(expected, actual, "Should correctly parse and return the set YearMonth");
	}
	
	@Test
	void testGetValidYearMonth_InvalidInput_ThrowsException() {
		assertThrows(InvalidMonthYearExceptionHandler.class, () -> {
			ExpenseUtils.getValidYearMonth("2024/04");
		}, "Should throw InvalidMonthYearExceptionHandler for invalid month.");
	}
	
	@Test
	void testGetValidYearMonth_InvalidMonth_ThrowsException() {
		assertThrows(InvalidMonthYearExceptionHandler.class, () -> {
			ExpenseUtils.getValidYearMonth("2024-13");
		}, "Should throw InvalidMonthYearExceptionHandler for invalid month.");
	}
	
	@Test
	void testParseYearMonth_ValidInput_ReturnsParsedYearMonth() {
		YearMonth expected = YearMonth.of(2025, 3);
		YearMonth actual = ExpenseUtils.getValidYearMonth("2025-03");
		assertEquals(expected, actual, "Should correctly parse the valid YearMonth string");
	}
	
	@Test
	void testParseYearMonth_InvalidFormat_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            ExpenseUtils.parseYearMonth("11-2023");
        }, "Should throw IllegalArgumentException for invalid format");
	}
	
    @Test
    void testParseYearMonth_InvalidCharacters_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            ExpenseUtils.parseYearMonth("abcd-ef");
        }, "Should throw IllegalArgumentException for non-numeric input");
    }
    
    @Test
    void testBuildExpenseRequest_SetsValidMonthYear() {
        
    	ExpenseRequest request = new ExpenseRequest();
        request.setMonthYear("2022-08");
        
        ExpenseRequest result = ExpenseUtils.buildExpenseRequest(request);
        
        assertEquals("2022-08", result.getMonthYear(), "Should correctly set the monthYear");
    }

    @Test
    void testBuildExpense_CreatesExpenseWithValidValues() {
        
    	ExpenseRequest request = new ExpenseRequest();
        request.setName("Rent");
        request.setAmount(1000.0);
        request.setCategory(ExpenseCategory.HOUSING);
        request.setType(TransactionType.RECURRING);
        request.setMonthYear("2000-01");
        
        Expense expense = ExpenseUtils.buildExpense(request);
        
        assertNotNull(expense, "Expense object should not be null");
        assertEquals("Rent", expense.getName(), "Name should match");
        assertEquals(1000.0, expense.getAmount(), "Amount should match");
        assertEquals(ExpenseCategory.HOUSING, expense.getCategory(), "Expense category should match");
        assertEquals(TransactionType.RECURRING, expense.getType(), "Transaction type should match");
        assertEquals(YearMonth.of(2000, 1), expense.getMonthYear(), "MonthYear should be correctly set");
    }
    
    @Test
    void testModifyExpense_ValidRequest_ModifiesExpenseCorrectly() {
    	
    	Expense expense = new Expense("Rent", 1000.0, ExpenseCategory.HOUSING, TransactionType.RECURRING, YearMonth.of(2000, 1));
        
    	ExpenseRequest request = new ExpenseRequest();
        request.setName("Gas Bill");
        request.setAmount(100.0);
        request.setCategory(ExpenseCategory.UTILITIES);
        request.setType(TransactionType.RECURRING);
        request.setMonthYear("2020-01");

        ExpenseUtils.modifyExpense(expense, request);
        
        assertEquals("Gas Bill", expense.getName(), "Name should match");
        assertEquals(100.0, expense.getAmount(), "Amount should match");
        assertEquals(ExpenseCategory.UTILITIES, expense.getCategory(), "Expense category should match");
        assertEquals(TransactionType.RECURRING, expense.getType(), "Transaction type should match");
        assertEquals(YearMonth.of(2020, 1), expense.getMonthYear(), "MonthYear should be correctly set");
    }

    @Test
    void testModifyExpense_NullMonthYear_DoesNotChangeMonthYear() {
    	
        YearMonth originalMonthYear = YearMonth.of(2000, 1);
        
        Expense expense = new Expense("Rent", 1000.0, ExpenseCategory.HOUSING, TransactionType.RECURRING, YearMonth.of(2000, 1));
        
        ExpenseRequest request = new ExpenseRequest();
        request.setName("Gas Bill");
        request.setAmount(100.0);
        request.setCategory(ExpenseCategory.UTILITIES);
        request.setType(TransactionType.ONE_TIME);
        request.setMonthYear(null);

        ExpenseUtils.modifyExpense(expense, request);

        assertEquals(originalMonthYear, expense.getMonthYear(), "MonthYear should remain unchanged");
    }

    @Test
    void testModifyExpense_EmptyMonthYear_DoesNotChangeMonthYear() {
        
    	YearMonth originalMonthYear = YearMonth.of(2000, 1);
        
    	Expense expense = new Expense("Rent", 1000.0, ExpenseCategory.HOUSING, TransactionType.RECURRING, YearMonth.of(2000, 1));
        
    	ExpenseRequest request = new ExpenseRequest();
        request.setName("Gas Bill");
        request.setAmount(100.0);
        request.setCategory(ExpenseCategory.UTILITIES);
        request.setType(TransactionType.ONE_TIME);
        request.setMonthYear("");

        ExpenseUtils.modifyExpense(expense, request);

        assertEquals(originalMonthYear, expense.getMonthYear(), "MonthYear should remain unchanged");
    }
}