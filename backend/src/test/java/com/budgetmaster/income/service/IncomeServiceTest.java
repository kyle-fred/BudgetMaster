package com.budgetmaster.income.service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

import com.budgetmaster.budget.service.logic.IncomeBudgetSynchronizer;
import com.budgetmaster.common.enums.TransactionType;
import com.budgetmaster.common.utils.DateUtils;
import com.budgetmaster.income.dto.IncomeRequest;
import com.budgetmaster.income.exception.IncomeNotFoundException;
import com.budgetmaster.income.model.Income;
import com.budgetmaster.income.repository.IncomeRepository;
import com.budgetmaster.money.dto.MoneyRequest;
import com.budgetmaster.money.model.Money;
import com.budgetmaster.test.constants.TestData;
import com.budgetmaster.test.constants.TestMessages;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;


public class IncomeServiceTest {
	// -- Dependencies --
	private final IncomeRepository incomeRepository = mock(IncomeRepository.class);
	private final IncomeBudgetSynchronizer incomeBudgetSynchronizer = mock(IncomeBudgetSynchronizer.class);
	private final IncomeService incomeService = new IncomeService(incomeRepository, incomeBudgetSynchronizer);
	
	// -- Test Data --
	private static final Long testId = TestData.CommonTestDataConstants.ID_EXISTING;
	private static final Long testIdNonExistent = TestData.CommonTestDataConstants.ID_NON_EXISTING;
	private static final String testName = TestData.IncomeTestDataConstants.NAME;
	private static final String testSource = TestData.IncomeTestDataConstants.SOURCE;
	private static final BigDecimal testAmount = TestData.MoneyDtoTestDataConstants.AMOUNT;
	private static final Currency testCurrency = TestData.CurrencyTestDataConstants.CURRENCY_GBP;
	private static final TransactionType testType = TestData.IncomeTestDataConstants.TYPE_ONE_TIME;
	private static final String testMonth = TestData.MonthTestDataConstants.MONTH_STRING_EXISTING;
	private static final YearMonth testYearMonth = TestData.MonthTestDataConstants.MONTH_EXISTING;
	
	// -- Test Objects --
	private IncomeRequest incomeRequest;
	private Income testIncome;
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
		
		// Setup Income
		testIncome = new Income();
		testIncome.setId(testId);
		testIncome.setName(testName);
		testIncome.setSource(testSource);
		testIncome.setMoney(Money.of(testAmount, testCurrency));
		testIncome.setType(testType);
		testIncome.setMonth(testYearMonth);
	}
	
	// -- Create Income Tests --
	
	@Test
	void createIncome_ValidRequest_ReturnsCreated() {
		Mockito.when(incomeRepository.saveAndFlush(Mockito.any(Income.class)))
				.thenReturn(testIncome);
		
		Income savedIncome = incomeService.createIncome(incomeRequest);

		assertNotNull(savedIncome);
		assertEquals(testName, savedIncome.getName());
		assertEquals(testSource, savedIncome.getSource());
		assertEquals(testAmount, savedIncome.getMoney().getAmount());
		assertEquals(testCurrency, savedIncome.getMoney().getCurrency());
		assertEquals(testType, savedIncome.getType());
		assertEquals(testYearMonth, savedIncome.getMonth());

		Mockito.verify(incomeRepository, Mockito.times(1))
				.saveAndFlush(Mockito.any(Income.class));
	}
	
	// -- Get All Incomes Tests --
	
	@Test
	void getAllIncomes_ValidMonth_ReturnsOk() {
		List<Income> incomes = List.of(testIncome, testIncome);
		
		try (MockedStatic<DateUtils> mockedDateUtils = mockStatic(DateUtils.class)) {
			mockedDateUtils.when(() -> DateUtils.getValidYearMonth(testMonth))
					.thenReturn(testYearMonth);
			Mockito.when(incomeRepository.findByMonth(testYearMonth))
					.thenReturn(incomes);

			List<Income> result = incomeService.getAllIncomesForMonth(testMonth);

			assertNotNull(result);
			assertEquals(2, result.size());
			assertEquals(testName, result.get(0).getName());
			assertEquals(testName, result.get(1).getName());

			Mockito.verify(incomeRepository, Mockito.times(1))
					.findByMonth(testYearMonth);
		}
	}
	
	// -- Get Income Tests --
	
	@Test
	void getIncome_ValidId_ReturnsOk() {
		Mockito.when(incomeRepository.findById(testId))
				.thenReturn(Optional.of(testIncome));
		
		Income retrievedIncome = incomeService.getIncomeById(testId);
		
		assertNotNull(retrievedIncome);
		assertEquals(testName, retrievedIncome.getName());
		assertEquals(testSource, retrievedIncome.getSource());
		assertEquals(testAmount, retrievedIncome.getMoney().getAmount());
		assertEquals(testCurrency, retrievedIncome.getMoney().getCurrency());
		assertEquals(testType, retrievedIncome.getType());
		assertEquals(testYearMonth, retrievedIncome.getMonth());
		
		Mockito.verify(incomeRepository, Mockito.times(1))
				.findById(testId);
	}
	
	// -- Update Income Tests --
	
	@Test
	void updateIncome_ValidRequest_ReturnsOk() {
		Mockito.when(incomeRepository.findById(testId))
				.thenReturn(Optional.of(testIncome));
        
		Mockito.when(incomeRepository.saveAndFlush(Mockito.any(Income.class)))
				.thenReturn(testIncome);

        IncomeRequest updatedIncomeRequest = new IncomeRequest();
        updatedIncomeRequest.setName(TestData.IncomeTestDataConstants.NAME_UPDATED);
        updatedIncomeRequest.setSource(TestData.IncomeTestDataConstants.SOURCE_UPDATED);
        updatedIncomeRequest.setMoney(moneyRequest);
        updatedIncomeRequest.setType(TestData.IncomeTestDataConstants.TYPE_UPDATED);
        updatedIncomeRequest.setMonth(TestData.IncomeTestDataConstants.MONTH_UPDATED);

		Income updatedIncome = incomeService.updateIncome(testId, updatedIncomeRequest);

		assertNotNull(updatedIncome);
		assertEquals(updatedIncomeRequest.getName(), updatedIncome.getName());
		assertEquals(updatedIncomeRequest.getSource(), updatedIncome.getSource());
		assertEquals(updatedIncomeRequest.getMoney().getAmount(), updatedIncome.getMoney().getAmount());
		assertEquals(updatedIncomeRequest.getMoney().getCurrency(), updatedIncome.getMoney().getCurrency());
		assertEquals(updatedIncomeRequest.getType(), updatedIncome.getType());
		assertEquals(updatedIncomeRequest.getMonth(), updatedIncome.getMonth().toString());
		
		Mockito.verify(incomeRepository, Mockito.times(1))
				.saveAndFlush(Mockito.any(Income.class));
	}
	
	// -- Delete Income Tests --
	
	@Test
	void deleteIncome_ValidId_ReturnsNoContent() {
		Mockito.when(incomeRepository.findById(testId))
				.thenReturn(Optional.of(testIncome));
		Mockito.doNothing()
				.when(incomeRepository)
				.deleteById(testId);
		
		incomeService.deleteIncome(testId);

		Mockito.verify(incomeRepository, Mockito.times(1))
				.findById(testId);
		Mockito.verify(incomeRepository, Mockito.times(1))
				.deleteById(testId);
	}
	
	// -- Error Handling Tests --
	
	@Test
	void createIncome_ServiceError_ReturnsInternalServerError() {
		String errorMessage = TestMessages.CommonErrorMessageConstants.DUPLICATE_ENTRY;
		Mockito.when(incomeRepository.saveAndFlush(Mockito.any(Income.class)))
				.thenThrow(new DataIntegrityViolationException(errorMessage));
		
		DataIntegrityViolationException exception = assertThrows(
				DataIntegrityViolationException.class,
				() -> incomeService.createIncome(incomeRequest),
				errorMessage
		);
		
		assertEquals(errorMessage, exception.getMessage());
	}
	
	@Test
	void getIncome_NonExistentId_ReturnsNotFound() {
		String errorMessage = String.format(TestMessages.IncomeErrorMessageConstants.INCOME_NOT_FOUND_WITH_ID, testIdNonExistent);
		Mockito.when(incomeRepository.findById(testIdNonExistent))
				.thenReturn(Optional.empty());
		
		IncomeNotFoundException exception = assertThrows(
				IncomeNotFoundException.class,
				() -> incomeService.getIncomeById(testIdNonExistent),
				errorMessage
		);
		
		assertEquals(errorMessage, exception.getMessage());
	}
	
	@Test
	void getAllIncomes_NoIncomes_ReturnsNotFound() {
		String errorMessage = String.format(TestMessages.IncomeErrorMessageConstants.INCOME_NOT_FOUND_BY_MONTH, testYearMonth);
		
		try (MockedStatic<DateUtils> mockedDateUtils = mockStatic(DateUtils.class)) {
			mockedDateUtils.when(() -> DateUtils.getValidYearMonth(testMonth))
					.thenReturn(testYearMonth);
			Mockito.when(incomeRepository.findByMonth(testYearMonth))
					.thenThrow(new IncomeNotFoundException(errorMessage));

			IncomeNotFoundException exception = assertThrows(
					IncomeNotFoundException.class,
					() -> incomeService.getAllIncomesForMonth(testMonth),
					errorMessage
			);
			
			assertEquals(errorMessage, exception.getMessage());
		}
	}
	
	@Test
	void updateIncome_NonExistentId_ReturnsNotFound() {
		String errorMessage = String.format(TestMessages.IncomeErrorMessageConstants.INCOME_NOT_FOUND_WITH_ID, testIdNonExistent);
		Mockito.when(incomeRepository.findById(testIdNonExistent))
				.thenReturn(Optional.empty());

		IncomeNotFoundException exception = assertThrows(
				IncomeNotFoundException.class,
				() -> incomeService.updateIncome(testIdNonExistent, incomeRequest),
				errorMessage
		);
		
		assertEquals(errorMessage, exception.getMessage());
		Mockito.verify(incomeRepository, Mockito.never())
				.saveAndFlush(Mockito.any(Income.class));
	}
	
	@Test
	void deleteIncome_NonExistentId_ReturnsNotFound() {
		String errorMessage = String.format(TestMessages.IncomeErrorMessageConstants.INCOME_NOT_FOUND_WITH_ID, testIdNonExistent);
		Mockito.when(incomeRepository.findById(testIdNonExistent))
				.thenReturn(Optional.empty());
		
		IncomeNotFoundException exception = assertThrows(
				IncomeNotFoundException.class,
				() -> incomeService.deleteIncome(testIdNonExistent),
				errorMessage
		);
		
		assertEquals(errorMessage, exception.getMessage());
		Mockito.verify(incomeRepository, Mockito.never())
				.deleteById(Mockito.anyLong());
	}
}