package com.budgetmaster.service;

import com.budgetmaster.dto.IncomeRequest;
import com.budgetmaster.dto.money.MoneyRequest;
import com.budgetmaster.enums.SupportedCurrency;
import com.budgetmaster.enums.TransactionType;
import com.budgetmaster.repository.IncomeRepository;
import com.budgetmaster.utils.date.DateUtils;
import com.budgetmaster.model.Income;
import com.budgetmaster.model.value.Money;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

import com.budgetmaster.exception.IncomeNotFoundException;

class IncomeServiceTest {
	// -- Dependencies --
	private final IncomeRepository incomeRepository = mock(IncomeRepository.class);
	private final IncomeService incomeService = new IncomeService(incomeRepository);
	
	// -- Test Data --
	private static final Long testId = 1L;
	private static final String testName = "TEST INCOME";
	private static final String testSource = "TEST SOURCE";
	private static final BigDecimal testAmount = new BigDecimal("123.45");
	private static final Currency testCurrency = SupportedCurrency.GBP.getCurrency();
	private static final TransactionType testType = TransactionType.ONE_TIME;
	private static final String testMonth = "2000-01";
	private static final YearMonth testYearMonth = YearMonth.of(2000, 1);
	
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

		assertNotNull(savedIncome, "Income should not be null");
		assertEquals(testName, savedIncome.getName(), "Name should be equal to the test value");
		assertEquals(testSource, savedIncome.getSource(), "Source should be equal to the test value");
		assertEquals(testAmount, savedIncome.getMoney().getAmount(), "Amount should be equal to the test value");
		assertEquals(testCurrency, savedIncome.getMoney().getCurrency(), "Currency should be equal to the test value");
		assertEquals(testType, savedIncome.getType(), "Type should be equal to the test value");
		assertEquals(testYearMonth, savedIncome.getMonth(), "Month should be equal to the test value");

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

			assertNotNull(result, "Result should not be null");
			assertEquals(2, result.size(), "Result should have 2 incomes");
			assertEquals(testName, result.get(0).getName(), "Name should be equal to the test value");
			assertEquals(testName, result.get(1).getName(), "Name should be equal to the test value");

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
		
		assertNotNull(retrievedIncome, "Income should not be null");
		assertEquals(testName, retrievedIncome.getName(), "Name should be equal to the test value");
		assertEquals(testSource, retrievedIncome.getSource(), "Source should be equal to the test value");
		assertEquals(testAmount, retrievedIncome.getMoney().getAmount(), "Amount should be equal to the test value");
		assertEquals(testCurrency, retrievedIncome.getMoney().getCurrency(), "Currency should be equal to the test value");
		assertEquals(testType, retrievedIncome.getType(), "Type should be equal to the test value");
		assertEquals(testYearMonth, retrievedIncome.getMonth(), "Month should be equal to the test value");
		
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
        updatedIncomeRequest.setName(testName + " UPDATED");
        updatedIncomeRequest.setSource(testSource + " UPDATED");
        updatedIncomeRequest.setMoney(moneyRequest);
        updatedIncomeRequest.setType(testType);
        updatedIncomeRequest.setMonth(testMonth);

		Income updatedIncome = incomeService.updateIncome(testId, updatedIncomeRequest);

		assertNotNull(updatedIncome, "Updated income should not be null");
		assertEquals(updatedIncomeRequest.getName(), updatedIncome.getName(), "Name should be equal to the updated value");
		assertEquals(updatedIncomeRequest.getSource(), updatedIncome.getSource(), "Source should be equal to the updated value");
		assertEquals(updatedIncomeRequest.getMoney().getAmount(), updatedIncome.getMoney().getAmount(), "Amount should be equal to the updated value");
		assertEquals(updatedIncomeRequest.getMoney().getCurrency(), updatedIncome.getMoney().getCurrency(), "Currency should be equal to the updated value");
		assertEquals(updatedIncomeRequest.getType(), updatedIncome.getType(), "Type should be equal to the updated value");
		assertEquals(updatedIncomeRequest.getMonth(), updatedIncome.getMonth().toString(), "Month should be equal to the updated value");
		
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
		String errorMessage = "Duplicate Entry";
		Mockito.when(incomeRepository.saveAndFlush(Mockito.any(Income.class)))
				.thenThrow(new DataIntegrityViolationException(errorMessage));
		
		DataIntegrityViolationException exception = assertThrows(
				DataIntegrityViolationException.class,
				() -> incomeService.createIncome(incomeRequest),
				errorMessage
		);
		
		assertEquals(errorMessage, exception.getMessage(), "Exception message thrown should be equal to the error message");
	}
	
	@Test
	void getIncome_NonExistentId_ReturnsNotFound() {
		String errorMessage = "Income not found with id: 99";
		Mockito.when(incomeRepository.findById(99L))
				.thenReturn(Optional.empty());
		
		IncomeNotFoundException exception = assertThrows(
				IncomeNotFoundException.class,
				() -> incomeService.getIncomeById(99L),
				errorMessage
		);
		
		assertEquals(errorMessage, exception.getMessage(), "Exception message thrown should be equal to the error message");
	}
	
	@Test
	void getAllIncomes_NoIncomes_ReturnsNotFound() {
		String errorMessage = "No incomes found for month: " + testYearMonth;
		
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
			
			assertEquals(errorMessage, exception.getMessage(), "Exception message thrown should be equal to the error message");
		}
	}
	
	@Test
	void updateIncome_NonExistentId_ReturnsNotFound() {
		String errorMessage = "Income not found with id: 99";
		Mockito.when(incomeRepository.findById(99L))
				.thenReturn(Optional.empty());

		IncomeNotFoundException exception = assertThrows(
				IncomeNotFoundException.class,
				() -> incomeService.updateIncome(99L, incomeRequest),
				errorMessage
		);
		
		assertEquals(errorMessage, exception.getMessage(), "Exception message thrown should be equal to the error message");
		Mockito.verify(incomeRepository, Mockito.never())
				.saveAndFlush(Mockito.any(Income.class));
	}
	
	@Test
	void deleteIncome_NonExistentId_ReturnsNotFound() {
		String errorMessage = "Income not found with id: 99";
		Mockito.when(incomeRepository.findById(99L))
				.thenReturn(Optional.empty());
		
		IncomeNotFoundException exception = assertThrows(
				IncomeNotFoundException.class,
				() -> incomeService.deleteIncome(99L),
				errorMessage
		);
		
		assertEquals(errorMessage, exception.getMessage(), "Exception message thrown should be equal to the error message");
		Mockito.verify(incomeRepository, Mockito.never())
				.deleteById(Mockito.anyLong());
	}
}