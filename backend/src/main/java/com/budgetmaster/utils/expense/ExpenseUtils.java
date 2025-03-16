package com.budgetmaster.utils.expense;

import java.time.YearMonth;

import com.budgetmaster.dto.ExpenseRequest;
import com.budgetmaster.exception.InvalidMonthYearExceptionHandler;
import com.budgetmaster.model.Expense;

public class ExpenseUtils {
	
    /**
     * Checks if the provided YearMonth is null or empty, returns the current YearMonth if true.
     */
    public static YearMonth getValidYearMonth(String monthYearString) {
        if (monthYearString == null || monthYearString.isEmpty()) {
            return YearMonth.now();
        }
        try {
        	return parseYearMonth(monthYearString);
        } catch (IllegalArgumentException e) {
        	throw new InvalidMonthYearExceptionHandler("Invalid month value. Month must be between 01 and 12. Expected format: YYYY-MM.");
        }
    }
	
    /**
     * If the string is not a valid YearMonth, throws an IllegalArgumentException.
     */
    public static YearMonth parseYearMonth(String yearMonthString) throws IllegalArgumentException {
        try {
            return YearMonth.parse(yearMonthString);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid YearMonth value. Expected format: YYYY-MM.", e);
        }
    }
    
    /**
     * Builds the ExpenseRequest object by setting the proper monthYear format.
     */
	public static ExpenseRequest buildExpenseRequest(ExpenseRequest request) {
		request.setMonthYear(getValidYearMonth(request.getMonthYear()).toString());
		return request;
	}
	
    /**
     * Builds the Expense object by setting the proper monthYear format.
     */
	public static Expense buildExpense(ExpenseRequest request) {
	    return new Expense(
	    		request.getName(), 
	    		request.getAmount(),
	    		request.getCategory(),
	    		request.getType(),
	    		getValidYearMonth(request.getMonthYear())
	    	);
	}
	
    /**
     * Modifies an existing Expense object with values from ExpenseRequest.
     */
    public static void modifyExpense(Expense expense, ExpenseRequest request) {
    	expense.setName(request.getName());
    	expense.setAmount(request.getAmount());
    	expense.setCategory(request.getCategory());
    	expense.setType(request.getType());

        if (request.getMonthYear() != null && !request.getMonthYear().isEmpty()) {
            expense.setMonthYear(getValidYearMonth(request.getMonthYear()));
        }
    }
}