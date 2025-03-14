package com.budgetmaster.exception;

public class InvalidMonthYearExceptionHandler extends RuntimeException {
	
	// Adding serialVersionUID for future-proofing exception serialization if needed (e.g., for logging or persistent storage).
    private static final long serialVersionUID = 1L;
	
	public InvalidMonthYearExceptionHandler(String message) {
		super(message);
	}
}