package com.budgetmaster.exception;

public class BudgetNotFoundException extends RuntimeException {
	
	// Adding serialVersionUID for future-proofing exception serialization if needed (e.g., for logging or persistent storage).
    private static final long serialVersionUID = 1L;
	
	public BudgetNotFoundException(String message) {
		super(message);
	}
}