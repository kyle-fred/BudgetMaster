package com.budgetmaster.application.model;

import com.budgetmaster.application.dto.ExpenseRequest;
import com.budgetmaster.testsupport.builder.dto.ExpenseRequestBuilder;
import com.budgetmaster.testsupport.builder.model.ExpenseBuilder;
import com.budgetmaster.testsupport.constants.domain.ExpenseConstants;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExpenseTest {
	
	@Test
	void from_ValidRequest_ReturnsExpense() {
		ExpenseRequest expenseRequest = ExpenseRequestBuilder.defaultExpenseRequest().buildRequest();
		Expense expense = Expense.from(expenseRequest);
		
		assertNotNull(expense);
		assertEquals(ExpenseConstants.Default.NAME, expense.getName());
		assertEquals(ExpenseConstants.Default.AMOUNT, expense.getMoney().getAmount());
		assertEquals(ExpenseConstants.Default.CURRENCY, expense.getMoney().getCurrency());
		assertEquals(ExpenseConstants.Default.CATEGORY, expense.getCategory());
		assertEquals(ExpenseConstants.Default.TYPE, expense.getType());
		assertEquals(ExpenseConstants.Default.YEAR_MONTH, expense.getMonth());
	}
	
	@Test
	void updateFrom_ValidRequest_UpdatesExpense() {
		Expense expense = new Expense();
		ExpenseRequest expenseRequest = ExpenseRequestBuilder.defaultExpenseRequest().buildRequest();
		expense.updateFrom(expenseRequest);
		
		assertEquals(ExpenseConstants.Default.NAME, expense.getName());
		assertEquals(ExpenseConstants.Default.AMOUNT, expense.getMoney().getAmount());
		assertEquals(ExpenseConstants.Default.CURRENCY, expense.getMoney().getCurrency());
		assertEquals(ExpenseConstants.Default.CATEGORY, expense.getCategory());
		assertEquals(ExpenseConstants.Default.TYPE, expense.getType());
		assertEquals(ExpenseConstants.Default.YEAR_MONTH, expense.getMonth());
	}

	@Test
	void deepCopy_ReturnsNewExpenseWithSameValues() {
		Expense expense = ExpenseBuilder.defaultExpense().build();
		Expense copy = expense.deepCopy();

		assertNotSame(expense, copy);
		assertEquals(expense.getName(), copy.getName());
		assertEquals(expense.getMoney().getAmount(), copy.getMoney().getAmount());
		assertEquals(expense.getMoney().getCurrency(), copy.getMoney().getCurrency());
		assertEquals(expense.getCategory(), copy.getCategory());
		assertEquals(expense.getType(), copy.getType());
		assertEquals(expense.getMonth(), copy.getMonth());
	}

	@Test
	void of_WithValidParameters_CreatesExpenseWithCorrectValues() {
		Expense expense = ExpenseBuilder.defaultExpense().build();

		assertNotNull(expense);
		assertEquals(ExpenseConstants.Default.NAME, expense.getName());
		assertEquals(ExpenseConstants.Default.AMOUNT, expense.getMoney().getAmount());
		assertEquals(ExpenseConstants.Default.CURRENCY, expense.getMoney().getCurrency());
		assertEquals(ExpenseConstants.Default.CATEGORY, expense.getCategory());
		assertEquals(ExpenseConstants.Default.TYPE, expense.getType());
		assertEquals(ExpenseConstants.Default.YEAR_MONTH, expense.getMonth());
	}
}