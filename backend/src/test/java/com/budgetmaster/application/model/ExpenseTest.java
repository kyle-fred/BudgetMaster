package com.budgetmaster.application.model;

import com.budgetmaster.application.dto.ExpenseRequest;
import com.budgetmaster.testsupport.assertions.model.ExpenseModelAssertions;
import com.budgetmaster.testsupport.builder.dto.ExpenseRequestBuilder;
import com.budgetmaster.testsupport.builder.model.ExpenseBuilder;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;

public class ExpenseTest {

	private Expense expense;
	private ExpenseRequest defaultExpenseRequest;

	@BeforeEach
	void setUp() {
		expense = new Expense();
		defaultExpenseRequest = ExpenseRequestBuilder.defaultExpenseRequest().buildRequest();
	}
	
	@Test
	void from_ValidRequest_ReturnsExpense() {
		expense = Expense.from(defaultExpenseRequest);
		
		ExpenseModelAssertions.assertExpense(expense)
			.isDefaultExpense();
	}
	
	@Test
	void updateFrom_ValidRequest_UpdatesExpense() {
		expense.updateFrom(defaultExpenseRequest);
		
		ExpenseModelAssertions.assertExpense(expense)
			.isDefaultExpense();
	}

	@Test
	void deepCopy_ReturnsNewExpenseWithSameValues() {
		expense = ExpenseBuilder.defaultExpense().build();
		Expense copy = expense.deepCopy();

		ExpenseModelAssertions.assertExpense(copy)
			.isDefaultExpense();
	}

	@Test
	void of_WithValidParameters_CreatesExpenseWithCorrectValues() {
		expense = ExpenseBuilder.defaultExpense().build();

		ExpenseModelAssertions.assertExpense(expense)
			.isDefaultExpense();
	}
}