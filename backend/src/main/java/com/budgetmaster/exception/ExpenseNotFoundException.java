package com.budgetmaster.exception;

public class ExpenseNotFoundException extends RuntimeException {
        /*
     *  Added serialVersionUID to handle object serialization - while not used now, this prevents
     *  version conflicts if later needed to serialize exceptions (e.g., in distributed systems)
     */
    private static final long serialVersionUID = 1L;
	
	public ExpenseNotFoundException(String message) {
		super(message);
	}
}
