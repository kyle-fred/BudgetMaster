package com.budgetmaster.utils.model;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Currency;

import com.budgetmaster.dto.IncomeRequest;
import com.budgetmaster.dto.ExpenseRequest;
import com.budgetmaster.dto.money.MoneyRequest;
import com.budgetmaster.enums.ExpenseCategory;
import com.budgetmaster.enums.TransactionType;
import com.budgetmaster.model.Income;
import com.budgetmaster.model.Expense;
import com.budgetmaster.model.value.Money;
import com.budgetmaster.test.constants.TestData;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FinancialModelUtilsTest {
	// -- Test Data --
	private static final String testName = TestData.FinancialModelTestDataConstants.NAME;
	private static final String testSource = TestData.FinancialModelTestDataConstants.SOURCE;
	private static final BigDecimal testAmount = TestData.FinancialModelTestDataConstants.AMOUNT;
	private static final Currency testCurrency = TestData.CurrencyTestDataConstants.CURRENCY_GBP;
    private static final ExpenseCategory testCategory = TestData.FinancialModelTestDataConstants.CATEGORY_MISCELLANEOUS;
	private static final TransactionType testType = TestData.FinancialModelTestDataConstants.TYPE_ONE_TIME;
	private static final String testMonth = TestData.MonthTestDataConstants.MONTH_STRING_EXISTING;
	private static final YearMonth testYearMonth = TestData.MonthTestDataConstants.MONTH_EXISTING;
	
	// -- Test Objects --
	private IncomeRequest incomeRequest;
	private ExpenseRequest expenseRequest;
	private MoneyRequest moneyRequest;
	
	// -- Setup --
	@BeforeEach
	void setUp() {
		// Setup MoneyRequest
		moneyRequest = new MoneyRequest();
		moneyRequest.setAmount(testAmount);
		moneyRequest.setCurrency(testCurrency);
		
		// Setup IncomeRequest
		incomeRequest = new IncomeRequest();
		incomeRequest.setName(testName);
		incomeRequest.setSource(testSource);
		incomeRequest.setMoney(moneyRequest);
		incomeRequest.setType(testType);
		incomeRequest.setMonth(testMonth);
		
		// Setup ExpenseRequest
		expenseRequest = new ExpenseRequest();
		expenseRequest.setName(testName);
		expenseRequest.setMoney(moneyRequest);
		expenseRequest.setCategory(testCategory);
		expenseRequest.setType(testType);
		expenseRequest.setMonth(testMonth);
	}
	
	// -- Income Tests --
	@Test
	void buildIncome_ValidRequest_ReturnsIncome() {
		Income income = FinancialModelUtils.buildIncome(incomeRequest);
		
		assertNotNull(income);
		assertEquals(testName, income.getName());
		assertEquals(testSource, income.getSource());
		assertEquals(testAmount, income.getMoney().getAmount());
		assertEquals(testCurrency, income.getMoney().getCurrency());
		assertEquals(testType, income.getType());
		assertEquals(testYearMonth, income.getMonth());
	}
	
	@Test
	void modifyIncome_ValidRequest_UpdatesIncome() {
		Income income = new Income();
		income.setName(TestData.FinancialModelTestDataConstants.NAME_OLD);
		income.setSource(TestData.FinancialModelTestDataConstants.SOURCE_OLD);
		income.setMoney(Money.of(TestData.FinancialModelTestDataConstants.AMOUNT_OLD, testCurrency));
		income.setType(TestData.FinancialModelTestDataConstants.TYPE_OLD);
		income.setMonth(TestData.MonthTestDataConstants.MONTH_NON_EXISTING);
		
		FinancialModelUtils.modifyIncome(income, incomeRequest);
		
		assertEquals(testName, income.getName());
		assertEquals(testSource, income.getSource());
		assertEquals(testAmount, income.getMoney().getAmount());
		assertEquals(testCurrency, income.getMoney().getCurrency());
		assertEquals(testType, income.getType());
		assertEquals(testYearMonth, income.getMonth());
	}
	
	// -- Expense Tests --
	@Test
	void buildExpense_ValidRequest_ReturnsExpense() {
		Expense expense = FinancialModelUtils.buildExpense(expenseRequest);
		
		assertNotNull(expense);
		assertEquals(testName, expense.getName());
		assertEquals(testAmount, expense.getMoney().getAmount());
		assertEquals(testCurrency, expense.getMoney().getCurrency());
		assertEquals(testCategory, expense.getCategory());
		assertEquals(testType, expense.getType());
		assertEquals(testYearMonth, expense.getMonth());
	}
	
	@Test
	void modifyExpense_ValidRequest_UpdatesExpense() {
		Expense expense = new Expense();
		expense.setName(TestData.FinancialModelTestDataConstants.NAME_OLD);
		expense.setMoney(Money.of(TestData.FinancialModelTestDataConstants.AMOUNT_OLD, testCurrency));
		expense.setCategory(TestData.FinancialModelTestDataConstants.CATEGORY_OLD);
		expense.setType(TestData.FinancialModelTestDataConstants.TYPE_OLD);
		expense.setMonth(TestData.MonthTestDataConstants.MONTH_NON_EXISTING);
		
		FinancialModelUtils.modifyExpense(expense, expenseRequest);
		
		assertEquals(testName, expense.getName());
		assertEquals(testAmount, expense.getMoney().getAmount());
		assertEquals(testCurrency, expense.getMoney().getCurrency());
		assertEquals(testCategory, expense.getCategory());
		assertEquals(testType, expense.getType());
		assertEquals(testYearMonth, expense.getMonth());
	}
}
