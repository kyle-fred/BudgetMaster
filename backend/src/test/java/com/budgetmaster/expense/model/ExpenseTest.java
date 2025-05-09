package com.budgetmaster.expense.model;

import com.budgetmaster.expense.dto.ExpenseRequest;
import com.budgetmaster.test.constants.TestData.ExpenseTestConstants;
import com.budgetmaster.test.factory.ExpenseTestFactory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExpenseTest {
	
	@Test
	void from_ValidRequest_ReturnsExpense() {
		ExpenseRequest expenseRequest = ExpenseTestFactory.createDefaultExpenseRequest();
		Expense expense = Expense.from(expenseRequest);
		
		assertNotNull(expense);
		assertEquals(ExpenseTestConstants.Default.NAME, expense.getName());
		assertEquals(ExpenseTestConstants.Default.AMOUNT, expense.getMoney().getAmount());
		assertEquals(ExpenseTestConstants.Default.CURRENCY, expense.getMoney().getCurrency());
		assertEquals(ExpenseTestConstants.Default.CATEGORY, expense.getCategory());
		assertEquals(ExpenseTestConstants.Default.TYPE, expense.getType());
		assertEquals(ExpenseTestConstants.Default.YEAR_MONTH, expense.getMonth());
	}
	
	@Test
	void updateFrom_ValidRequest_UpdatesExpense() {
		Expense expense = new Expense();
		ExpenseRequest expenseRequest = ExpenseTestFactory.createDefaultExpenseRequest();
		expense.updateFrom(expenseRequest);
		
		assertEquals(ExpenseTestConstants.Default.NAME, expense.getName());
		assertEquals(ExpenseTestConstants.Default.AMOUNT, expense.getMoney().getAmount());
		assertEquals(ExpenseTestConstants.Default.CURRENCY, expense.getMoney().getCurrency());
		assertEquals(ExpenseTestConstants.Default.CATEGORY, expense.getCategory());
		assertEquals(ExpenseTestConstants.Default.TYPE, expense.getType());
		assertEquals(ExpenseTestConstants.Default.YEAR_MONTH, expense.getMonth());
	}

	@Test
	void deepCopy_ReturnsNewExpenseWithSameValues() {
		Expense expense = ExpenseTestFactory.createDefaultExpense();
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
		Expense expense = ExpenseTestFactory.createDefaultExpense();

		assertNotNull(expense);
		assertEquals(ExpenseTestConstants.Default.NAME, expense.getName());
		assertEquals(ExpenseTestConstants.Default.AMOUNT, expense.getMoney().getAmount());
		assertEquals(ExpenseTestConstants.Default.CURRENCY, expense.getMoney().getCurrency());
		assertEquals(ExpenseTestConstants.Default.CATEGORY, expense.getCategory());
		assertEquals(ExpenseTestConstants.Default.TYPE, expense.getType());
		assertEquals(ExpenseTestConstants.Default.YEAR_MONTH, expense.getMonth());
	}
}