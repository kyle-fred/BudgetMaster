package com.budgetmaster.model;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Currency;

import com.budgetmaster.common.enums.TransactionType;
import com.budgetmaster.income.dto.IncomeRequest;
import com.budgetmaster.income.model.Income;
import com.budgetmaster.money.dto.MoneyRequest;
import com.budgetmaster.test.constants.TestData;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IncomeTest {
    	// -- Test Data --
	private static final String testName = TestData.IncomeTestDataConstants.NAME;
	private static final String testSource = TestData.IncomeTestDataConstants.SOURCE;
	private static final BigDecimal testAmount = TestData.IncomeTestDataConstants.AMOUNT;
	private static final Currency testCurrency = TestData.CurrencyTestDataConstants.CURRENCY_GBP;
	private static final TransactionType testType = TestData.IncomeTestDataConstants.TYPE_ONE_TIME;
	private static final String testMonth = TestData.MonthTestDataConstants.MONTH_STRING_EXISTING;
	private static final YearMonth testYearMonth = TestData.MonthTestDataConstants.MONTH_EXISTING;
	
	// -- Test Objects --
	private IncomeRequest incomeRequest;
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
	}
	
	@Test
	void from_ValidRequest_ReturnsIncome() {
		Income income = Income.from(incomeRequest);
		
		assertNotNull(income);
		assertEquals(testName, income.getName());
		assertEquals(testSource, income.getSource());
		assertEquals(testAmount, income.getMoney().getAmount());
		assertEquals(testCurrency, income.getMoney().getCurrency());
		assertEquals(testType, income.getType());
		assertEquals(testYearMonth, income.getMonth());
	}
	
	@Test
	void updateFrom_ValidRequest_UpdatesIncome() {
		Income income = new Income();
		income.updateFrom(incomeRequest);
		
		assertEquals(testName, income.getName());
		assertEquals(testSource, income.getSource());
		assertEquals(testAmount, income.getMoney().getAmount());
		assertEquals(testCurrency, income.getMoney().getCurrency());
		assertEquals(testType, income.getType());
		assertEquals(testYearMonth, income.getMonth());
	}
}
