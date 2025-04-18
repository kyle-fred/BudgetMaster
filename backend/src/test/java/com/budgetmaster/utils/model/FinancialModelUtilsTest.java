package com.budgetmaster.utils.model;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Currency;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.budgetmaster.dto.IncomeRequest;
import com.budgetmaster.dto.ExpenseRequest;
import com.budgetmaster.dto.money.MoneyRequest;
import com.budgetmaster.enums.ExpenseCategory;
import com.budgetmaster.enums.SupportedCurrency;
import com.budgetmaster.enums.TransactionType;
import com.budgetmaster.model.Income;
import com.budgetmaster.model.Expense;
import com.budgetmaster.model.value.Money;

public class FinancialModelUtilsTest {
	// -- Test Data --
	private static final String testName = "TEST NAME";
	private static final String testSource = "TEST SOURCE";
	private static final BigDecimal testAmount = new BigDecimal("123.45");
	private static final Currency testCurrency = SupportedCurrency.GBP.getCurrency();
    private static final ExpenseCategory testCategory = ExpenseCategory.MISCELLANEOUS;
	private static final TransactionType testType = TransactionType.RECURRING;
	private static final String testMonth = "2000-01";
	private static final YearMonth testYearMonth = YearMonth.of(2000, 1);
	
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
		
		assertNotNull(income, "Income should not be null");
		assertEquals(testName, income.getName(), "Name should match");
		assertEquals(testSource, income.getSource(), "Source should match");
		assertEquals(testAmount, income.getMoney().getAmount(), "Amount should match");
		assertEquals(testCurrency, income.getMoney().getCurrency(), "Currency should match");
		assertEquals(testType, income.getType(), "Transaction type should match");
		assertEquals(testYearMonth, income.getMonth(), "Month should match");
	}
	
	@Test
	void modifyIncome_ValidRequest_UpdatesIncome() {
		Income income = new Income();
		income.setName("OLD NAME");
		income.setSource("OLD SOURCE");
		income.setMoney(Money.of(new BigDecimal("1000.00"), SupportedCurrency.GBP.getCurrency()));
		income.setType(TransactionType.ONE_TIME);
		income.setMonth(YearMonth.of(1999, 12));
		
		FinancialModelUtils.modifyIncome(income, incomeRequest);
		
		assertEquals(testName, income.getName(), "Name should be updated");
		assertEquals(testSource, income.getSource(), "Source should be updated");
		assertEquals(testAmount, income.getMoney().getAmount(), "Amount should be updated");
		assertEquals(testCurrency, income.getMoney().getCurrency(), "Currency has not been updated");
		assertEquals(testType, income.getType(), "Transaction type should be updated");
		assertEquals(testYearMonth, income.getMonth(), "Month should be updated");
	}
	
	// -- Expense Tests --
	@Test
	void buildExpense_ValidRequest_ReturnsExpense() {
		Expense expense = FinancialModelUtils.buildExpense(expenseRequest);
		
		assertNotNull(expense, "Expense should not be null");
		assertEquals(testName, expense.getName(), "Name should match");
		assertEquals(testAmount, expense.getMoney().getAmount(), "Amount should match");
		assertEquals(testCurrency, expense.getMoney().getCurrency(), "Currency should match");
		assertEquals(testCategory, expense.getCategory(), "Category should match");
		assertEquals(testType, expense.getType(), "Transaction type should match");
		assertEquals(testYearMonth, expense.getMonth(), "Month should match");
	}
	
	@Test
	void modifyExpense_ValidRequest_UpdatesExpense() {
		Expense expense = new Expense();
		expense.setName("OLD NAME");
		expense.setMoney(Money.of(new BigDecimal("1000.00"), SupportedCurrency.GBP.getCurrency()));
		expense.setCategory(ExpenseCategory.GROCERIES);
		expense.setType(TransactionType.ONE_TIME);
		expense.setMonth(YearMonth.of(1999, 12));
		
		FinancialModelUtils.modifyExpense(expense, expenseRequest);
		
		assertEquals(testName, expense.getName(), "Name should be updated");
		assertEquals(testAmount, expense.getMoney().getAmount(), "Amount should be updated");
		assertEquals(testCurrency, expense.getMoney().getCurrency(), "Currency has not been updated");
		assertEquals(testCategory, expense.getCategory(), "Category should be updated");
		assertEquals(testType, expense.getType(), "Transaction type should be updated");
		assertEquals(testYearMonth, expense.getMonth(), "Month should be updated");
	}
}
