package com.budgetmaster.application.model;

import com.budgetmaster.application.dto.ExpenseRequest;
import com.budgetmaster.testsupport.expense.constants.ExpenseConstants;
import com.budgetmaster.testsupport.expense.factory.ExpenseFactory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExpenseTest {
	
	@Test
	void from_ValidRequest_ReturnsExpense() {
		ExpenseRequest expenseRequest = ExpenseFactory.createDefaultExpenseRequest();
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
		ExpenseRequest expenseRequest = ExpenseFactory.createDefaultExpenseRequest();
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
		Expense expense = ExpenseFactory.createDefaultExpense();
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
		Expense expense = ExpenseFactory.createDefaultExpense();

		assertNotNull(expense);
		assertEquals(ExpenseConstants.Default.NAME, expense.getName());
		assertEquals(ExpenseConstants.Default.AMOUNT, expense.getMoney().getAmount());
		assertEquals(ExpenseConstants.Default.CURRENCY, expense.getMoney().getCurrency());
		assertEquals(ExpenseConstants.Default.CATEGORY, expense.getCategory());
		assertEquals(ExpenseConstants.Default.TYPE, expense.getType());
		assertEquals(ExpenseConstants.Default.YEAR_MONTH, expense.getMonth());
	}
}