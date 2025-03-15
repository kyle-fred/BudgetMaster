package com.budgetmaster.utils.income;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.YearMonth;

import org.junit.jupiter.api.Test;

import com.budgetmaster.dto.IncomeRequest;
import com.budgetmaster.enums.TransactionType;
import com.budgetmaster.exception.InvalidMonthYearExceptionHandler;
import com.budgetmaster.model.Income;

public class IncomeUtilsTest {
	
	@Test
	void testGetValidYearMonth_NullInput_ReturnsCurrentYearMonth() {
		YearMonth expected = YearMonth.now();
		YearMonth actual = IncomeUtils.getValidYearMonth(null);
		assertNotNull(actual);
		assertEquals(expected, actual, "Should return current YearMonth when input is null");
	}
	
	@Test
	void testGetValidYearMonth_EmptyInput_ReturnsCurrentYearMonth() {
		YearMonth expected = YearMonth.now();
		YearMonth actual = IncomeUtils.getValidYearMonth("");
		assertNotNull(actual);
		assertEquals(expected, actual, "Should return current YearMonth when input is empty");
	}
	
	@Test
	void testGetValidYearMonth_ValidInput_ReturnsParsedYearMonth() {
		YearMonth expected = YearMonth.of(2025, 3);
		YearMonth actual = IncomeUtils.getValidYearMonth("2025-03");
		assertNotNull(actual);
		assertEquals(expected, actual, "Should correctly parse and return the set YearMonth");
	}
	
	@Test
	void testGetValidYearMonth_InvalidInput_ThrowsException() {
		assertThrows(InvalidMonthYearExceptionHandler.class, () -> {
			IncomeUtils.getValidYearMonth("2024/04");
		}, "Should throw InvalidMonthYearExceptionHandler for invalid month.");
	}
	
	@Test
	void testGetValidYearMonth_InvalidMonth_ThrowsException() {
		assertThrows(InvalidMonthYearExceptionHandler.class, () -> {
			IncomeUtils.getValidYearMonth("2024-13");
		}, "Should throw InvalidMonthYearExceptionHandler for invalid month.");
	}
	
	@Test
	void testParseYearMonth_ValidInput_ReturnsParsedYearMonth() {
		YearMonth expected = YearMonth.of(2025, 3);
		YearMonth actual = IncomeUtils.getValidYearMonth("2025-03");
		assertEquals(expected, actual, "Should correctly parse the valid YearMonth string");
	}
	
	@Test
	void testParseYearMonth_InvalidFormat_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            IncomeUtils.parseYearMonth("11-2023");
        }, "Should throw IllegalArgumentException for invalid format");
	}
	
    @Test
    void testParseYearMonth_InvalidCharacters_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            IncomeUtils.parseYearMonth("abcd-ef");
        }, "Should throw IllegalArgumentException for non-numeric input");
    }
    
    @Test
    void testBuildIncomeRequest_SetsValidMonthYear() {
        
    	IncomeRequest request = new IncomeRequest();
        request.setMonthYear("2022-08");
        
        IncomeRequest result = IncomeUtils.buildIncomeRequest(request);
        
        assertEquals("2022-08", result.getMonthYear(), "Should correctly set the monthYear");
    }

    @Test
    void testBuildIncome_CreatesIncomeWithValidValues() {
        
    	IncomeRequest request = new IncomeRequest();
        request.setName("Salary");
        request.setSource("Company XYZ");
        request.setAmount(2000.0);
        request.setType(TransactionType.RECURRING);
        request.setMonthYear("2000-01");
        
        Income income = IncomeUtils.buildIncome(request);
        
        assertNotNull(income, "Income object should not be null");
        assertEquals("Salary", income.getName(), "Name should match");
        assertEquals("Company XYZ", income.getSource(), "Source should match");
        assertEquals(2000.0, income.getAmount(), "Amount should match");
        assertEquals(TransactionType.RECURRING, income.getType(), "Transaction type should match");
        assertEquals(YearMonth.of(2000, 1), income.getMonthYear(), "MonthYear should be correctly set");
    }
    
    @Test
    void testModifyIncome_ValidRequest_ModifiesIncomeCorrectly() {
    	
    	Income income = new Income("Salary", "Company XYZ", 2000.0, TransactionType.RECURRING, YearMonth.of(2000, 1));
        IncomeRequest request = new IncomeRequest();
        request.setName("Bonus");
        request.setSource("Company ABC");
        request.setAmount(3000.0);
        request.setType(TransactionType.ONE_TIME);
        request.setMonthYear("2020-01");

        IncomeUtils.modifyIncome(income, request);
        
        assertEquals("Bonus", income.getName(), "Name should match");
        assertEquals("Company ABC", income.getSource(), "Source should match");
        assertEquals(3000.0, income.getAmount(), "Amount should match");
        assertEquals(TransactionType.ONE_TIME, income.getType(), "Transaction type should match");
        assertEquals(YearMonth.of(2020, 1), income.getMonthYear(), "MonthYear should be correctly set");
    }

    @Test
    void testModifyIncome_NullMonthYear_DoesNotChangeMonthYear() {
    	
        YearMonth originalMonthYear = YearMonth.of(2000, 1);
        
        Income income = new Income("Salary", "Company XYZ", 2000.0, TransactionType.RECURRING, originalMonthYear);
        IncomeRequest request = new IncomeRequest();
        request.setName("Bonus");
        request.setSource("Company ABC");
        request.setAmount(3000.0);
        request.setType(TransactionType.ONE_TIME);
        request.setMonthYear(null);

        IncomeUtils.modifyIncome(income, request);

        assertEquals(originalMonthYear, income.getMonthYear(), "MonthYear should remain unchanged");
    }

    @Test
    void testModifyIncome_EmptyMonthYear_DoesNotChangeMonthYear() {
        
    	YearMonth originalMonthYear = YearMonth.of(2000, 1);
        
        Income income = new Income("Salary", "Company XYZ", 2000.0, TransactionType.RECURRING, originalMonthYear);
        IncomeRequest request = new IncomeRequest();
        request.setName("Bonus");
        request.setSource("Company ABC");
        request.setAmount(3000.0);
        request.setType(TransactionType.ONE_TIME);
        request.setMonthYear("");

        IncomeUtils.modifyIncome(income, request);

        assertEquals(originalMonthYear, income.getMonthYear(), "MonthYear should remain unchanged");
    }
}