package com.budgetmaster.utils.income;

import java.time.YearMonth;

import com.budgetmaster.dto.BudgetRequest;
import com.budgetmaster.dto.IncomeRequest;
import com.budgetmaster.exception.InvalidMonthYearExceptionHandler;
import com.budgetmaster.model.Budget;
import com.budgetmaster.model.Income;

public class IncomeUtils {
	
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
     * Builds the IncomeRequest object by setting the proper monthYear format.
     */
	public static IncomeRequest buildIncomeRequest(IncomeRequest request) {
		request.setMonthYear(getValidYearMonth(request.getMonthYear()).toString());
		return request;
	}
	
    /**
     * Builds the Income object by setting the proper monthYear format.
     */
	public static Income buildIncome(IncomeRequest request) {
	    return new Income(
	    		request.getName(), 
	    		request.getSource(),
	    		request.getAmount(),
	    		request.getType(),
	    		getValidYearMonth(request.getMonthYear())
	    	);
	}
	
    /**
     * Modifies an existing Income object with values from IncomeRequest.
     */
    public static void modifyIncome(Income income, IncomeRequest request) {
    	income.setName(request.getName());
    	income.setSource(request.getSource());
    	income.setAmount(request.getAmount());
    	income.setType(request.getType());

        if (request.getMonthYear() != null && !request.getMonthYear().isEmpty()) {
            income.setMonthYear(getValidYearMonth(request.getMonthYear()));
        }
    }
}