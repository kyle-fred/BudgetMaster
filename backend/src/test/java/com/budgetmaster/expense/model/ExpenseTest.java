package com.budgetmaster.expense.model;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Currency;

import com.budgetmaster.common.enums.TransactionType;
import com.budgetmaster.expense.dto.ExpenseRequest;
import com.budgetmaster.expense.enums.ExpenseCategory;
import com.budgetmaster.money.dto.MoneyRequest;
import com.budgetmaster.test.constants.TestData;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExpenseTest {
    // -- Test Data --
	private static final String testName = TestData.ExpenseTestDataConstants.NAME;
	private static final BigDecimal testAmount = TestData.ExpenseTestDataConstants.AMOUNT;
	private static final ExpenseCategory testCategory = TestData.ExpenseTestDataConstants.CATEGORY_MISCELLANEOUS;
	private static final Currency testCurrency = TestData.CurrencyTestDataConstants.CURRENCY_GBP;
	private static final TransactionType testType = TestData.ExpenseTestDataConstants.TYPE_ONE_TIME;
	private static final String testMonth = TestData.MonthTestDataConstants.MONTH_STRING_EXISTING;
	private static final YearMonth testYearMonth = TestData.MonthTestDataConstants.MONTH_EXISTING;
	
	// -- Test Objects --
	private ExpenseRequest expenseRequest;
	private MoneyRequest moneyRequest;
	
	// -- Setup --
	@BeforeEach
	void setUp() {
		// Setup MoneyRequest
		moneyRequest = new MoneyRequest();
		moneyRequest.setAmount(testAmount);
		moneyRequest.setCurrency(testCurrency);
		
		// Setup ExpenseRequest
		expenseRequest = new ExpenseRequest();
		expenseRequest.setName(testName);
		expenseRequest.setMoney(moneyRequest);
		expenseRequest.setCategory(testCategory);
		expenseRequest.setType(testType);
		expenseRequest.setMonth(testMonth);
	}
	
	@Test
	void from_ValidRequest_ReturnsExpense() {
		Expense expense = Expense.from(expenseRequest);
		
		assertNotNull(expense);
		assertEquals(testName, expense.getName());
		assertEquals(testAmount, expense.getMoney().getAmount());
		assertEquals(testCurrency, expense.getMoney().getCurrency());
		assertEquals(testCategory, expense.getCategory());
		assertEquals(testType, expense.getType());
		assertEquals(testYearMonth, expense.getMonth());
	}
	
	@Test
	void updateFrom_ValidRequest_UpdatesExpense() {
		Expense expense = new Expense();
		expense.updateFrom(expenseRequest);
		
		assertEquals(testName, expense.getName());
		assertEquals(testAmount, expense.getMoney().getAmount());
		assertEquals(testCurrency, expense.getMoney().getCurrency());
		assertEquals(testCategory, expense.getCategory());
		assertEquals(testType, expense.getType());
		assertEquals(testYearMonth, expense.getMonth());
	}
}
