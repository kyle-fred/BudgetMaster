package com.budgetmaster.utils;

import java.time.YearMonth;

import com.budgetmaster.dto.BudgetRequest;
import com.budgetmaster.exception.InvalidMonthYearException;
import com.budgetmaster.model.Budget;

public class BudgetUtils {
	
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
        	throw new InvalidMonthYearException("Invalid month value. Month must be between 01 and 12. Expected format: YYYY-MM.");
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
     * Builds the BudgetRequest object by setting the proper monthYear format.
     */
	public static BudgetRequest buildBudgetRequest(BudgetRequest request) {
		request.setMonthYear(getValidYearMonth(request.getMonthYear()).toString());
		return request;
	}
	
    /**
     * Builds the Budget object by setting the proper monthYear format.
     */
	public static Budget buildBudget(BudgetRequest request) {
	    return new Budget(
	    		request.getIncome(), 
	    		request.getExpenses(), 
	    		getValidYearMonth(request.getMonthYear())
	    	);
	}
}