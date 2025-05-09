package com.budgetmaster.income.service;

import java.util.List;
import java.util.Optional;

import com.budgetmaster.budget.service.logic.IncomeBudgetSynchronizer;
import com.budgetmaster.common.utils.DateUtils;
import com.budgetmaster.config.JacksonConfig;
import com.budgetmaster.income.dto.IncomeRequest;
import com.budgetmaster.income.exception.IncomeNotFoundException;
import com.budgetmaster.income.model.Income;
import com.budgetmaster.income.repository.IncomeRepository;
import com.budgetmaster.test.constants.TestMessages;
import com.budgetmaster.test.constants.TestData.IncomeTestConstants;
import com.budgetmaster.test.factory.IncomeTestFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

@Import(JacksonConfig.class)
public class IncomeServiceTest {
	// -- Dependencies --
	private final IncomeRepository incomeRepository = mock(IncomeRepository.class);
	private final IncomeBudgetSynchronizer incomeBudgetSynchronizer = mock(IncomeBudgetSynchronizer.class);
	private final IncomeService incomeService = new IncomeService(incomeRepository, incomeBudgetSynchronizer);
	
	// -- Test Objects --
	private Income testIncome;
	private IncomeRequest incomeRequest;
	
	@BeforeEach
	void setUp() {
		testIncome = IncomeTestFactory.createDefaultIncome();
		incomeRequest = IncomeTestFactory.createDefaultIncomeRequest();
	}
	
	@Test
	void createIncome_ValidRequest_ReturnsCreated() {
		Mockito.when(incomeRepository.saveAndFlush(Mockito.any(Income.class)))
				.thenReturn(testIncome);
		Mockito.doNothing().when(incomeBudgetSynchronizer)
				.apply(Mockito.any(Income.class));
		
		Income savedIncome = incomeService.createIncome(incomeRequest);

		assertNotNull(savedIncome);
		assertEquals(IncomeTestConstants.Default.NAME, savedIncome.getName());
		assertEquals(IncomeTestConstants.Default.SOURCE, savedIncome.getSource());
		assertEquals(IncomeTestConstants.Default.AMOUNT, savedIncome.getMoney().getAmount());
		assertEquals(IncomeTestConstants.Default.CURRENCY, savedIncome.getMoney().getCurrency());
		assertEquals(IncomeTestConstants.Default.TYPE, savedIncome.getType());
		assertEquals(IncomeTestConstants.Default.YEAR_MONTH, savedIncome.getMonth());

		Mockito.verify(incomeBudgetSynchronizer, Mockito.times(1))
				.apply(testIncome);
		Mockito.verify(incomeRepository, Mockito.times(1))
				.saveAndFlush(Mockito.any(Income.class));
	}
	
	@Test
	void getAllIncomes_ValidMonth_ReturnsOk() {
		List<Income> incomes = List.of(testIncome, testIncome);
		
		try (MockedStatic<DateUtils> mockedDateUtils = mockStatic(DateUtils.class)) {
			mockedDateUtils.when(() -> DateUtils.getValidYearMonth(IncomeTestConstants.Default.YEAR_MONTH.toString()))
					.thenReturn(IncomeTestConstants.Default.YEAR_MONTH);
			Mockito.when(incomeRepository.findByMonth(IncomeTestConstants.Default.YEAR_MONTH))
					.thenReturn(incomes);

			List<Income> result = incomeService.getAllIncomesForMonth(IncomeTestConstants.Default.YEAR_MONTH.toString());

			assertNotNull(result);
			assertEquals(2, result.size());
			assertEquals(IncomeTestConstants.Default.NAME, result.get(0).getName());
			assertEquals(IncomeTestConstants.Default.NAME, result.get(1).getName());

			Mockito.verify(incomeRepository, Mockito.times(1))
					.findByMonth(IncomeTestConstants.Default.YEAR_MONTH);
		}
	}
	
	@Test
	void getIncome_ValidId_ReturnsOk() {
		Mockito.when(incomeRepository.findById(IncomeTestConstants.Default.ID))
				.thenReturn(Optional.of(testIncome));
		
		Income retrievedIncome = incomeService.getIncomeById(IncomeTestConstants.Default.ID);
		
		assertNotNull(retrievedIncome);
		assertEquals(IncomeTestConstants.Default.NAME, retrievedIncome.getName());
		assertEquals(IncomeTestConstants.Default.SOURCE, retrievedIncome.getSource());
		assertEquals(IncomeTestConstants.Default.AMOUNT, retrievedIncome.getMoney().getAmount());
		assertEquals(IncomeTestConstants.Default.CURRENCY, retrievedIncome.getMoney().getCurrency());
		assertEquals(IncomeTestConstants.Default.TYPE, retrievedIncome.getType());
		assertEquals(IncomeTestConstants.Default.YEAR_MONTH, retrievedIncome.getMonth());
		
		Mockito.verify(incomeRepository, Mockito.times(1))
				.findById(IncomeTestConstants.Default.ID);
	}
	
	@Test
	void updateIncome_ValidRequest_ReturnsOk() {
		IncomeRequest updatedRequest = IncomeTestFactory.createUpdatedIncomeRequest();

		Mockito.when(incomeRepository.findById(IncomeTestConstants.Default.ID))
				.thenReturn(Optional.of(testIncome));
		Mockito.doNothing().when(incomeBudgetSynchronizer)
				.reapply(Mockito.any(Income.class), Mockito.any(Income.class));
        
		Mockito.when(incomeRepository.saveAndFlush(Mockito.any(Income.class)))
				.thenReturn(testIncome);

		Income updatedIncome = incomeService.updateIncome(IncomeTestConstants.Default.ID, updatedRequest);

		assertNotNull(updatedIncome);
		assertEquals(IncomeTestConstants.Updated.NAME, updatedIncome.getName());
		assertEquals(IncomeTestConstants.Updated.SOURCE, updatedIncome.getSource());
		assertEquals(IncomeTestConstants.Updated.AMOUNT, updatedIncome.getMoney().getAmount());
		assertEquals(IncomeTestConstants.Default.CURRENCY, updatedIncome.getMoney().getCurrency());
		assertEquals(IncomeTestConstants.Updated.TYPE, updatedIncome.getType());
		assertEquals(IncomeTestConstants.Updated.YEAR_MONTH, updatedIncome.getMonth());
		
		Mockito.verify(incomeBudgetSynchronizer, Mockito.times(1))
				.reapply(Mockito.any(Income.class), Mockito.any(Income.class));
		Mockito.verify(incomeRepository, Mockito.times(1))
				.saveAndFlush(Mockito.any(Income.class));
	}
	
	@Test
	void deleteIncome_ValidId_ReturnsNoContent() {
		Mockito.when(incomeRepository.findById(IncomeTestConstants.Default.ID))
				.thenReturn(Optional.of(testIncome));
		Mockito.doNothing().when(incomeBudgetSynchronizer)
				.retract(Mockito.any(Income.class));
		Mockito.doNothing()
				.when(incomeRepository)
				.deleteById(IncomeTestConstants.Default.ID);
		
		incomeService.deleteIncome(IncomeTestConstants.Default.ID);

		Mockito.verify(incomeBudgetSynchronizer, Mockito.times(1))
				.retract(Mockito.any(Income.class));
		Mockito.verify(incomeRepository, Mockito.times(1))
				.findById(IncomeTestConstants.Default.ID);
		Mockito.verify(incomeRepository, Mockito.times(1))
				.deleteById(IncomeTestConstants.Default.ID);
	}
	
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
		Mockito.verify(incomeBudgetSynchronizer, Mockito.never())
				.apply(Mockito.any(Income.class));
	}
	
	@Test
	void getIncome_NonExistentId_ReturnsNotFound() {
		String errorMessage = String.format(TestMessages.IncomeErrorMessageConstants.INCOME_NOT_FOUND_WITH_ID, IncomeTestConstants.NonExistent.ID);
		Mockito.when(incomeRepository.findById(IncomeTestConstants.NonExistent.ID))
				.thenReturn(Optional.empty());
		
		IncomeNotFoundException exception = assertThrows(
				IncomeNotFoundException.class,
				() -> incomeService.getIncomeById(IncomeTestConstants.NonExistent.ID),
				errorMessage
		);
		
		assertEquals(errorMessage, exception.getMessage());
	}
	
	@Test
	void getAllIncomes_NoIncomes_ReturnsNotFound() {
		String errorMessage = String.format(TestMessages.IncomeErrorMessageConstants.INCOME_NOT_FOUND_BY_MONTH, IncomeTestConstants.NonExistent.YEAR_MONTH);
		
		try (MockedStatic<DateUtils> mockedDateUtils = mockStatic(DateUtils.class)) {
			mockedDateUtils.when(() -> DateUtils.getValidYearMonth(IncomeTestConstants.NonExistent.YEAR_MONTH.toString()))
					.thenReturn(IncomeTestConstants.NonExistent.YEAR_MONTH);
			Mockito.when(incomeRepository.findByMonth(IncomeTestConstants.NonExistent.YEAR_MONTH))
					.thenThrow(new IncomeNotFoundException(errorMessage));

			IncomeNotFoundException exception = assertThrows(
					IncomeNotFoundException.class,
					() -> incomeService.getAllIncomesForMonth(IncomeTestConstants.NonExistent.YEAR_MONTH.toString()),
					errorMessage
			);
			
			assertEquals(errorMessage, exception.getMessage());
		}
	}
	
	@Test
	void updateIncome_NonExistentId_ReturnsNotFound() {
		String errorMessage = String.format(TestMessages.IncomeErrorMessageConstants.INCOME_NOT_FOUND_WITH_ID, IncomeTestConstants.NonExistent.ID);
		Mockito.when(incomeRepository.findById(IncomeTestConstants.NonExistent.ID))
				.thenReturn(Optional.empty());
		Mockito.doNothing().when(incomeBudgetSynchronizer)
				.reapply(Mockito.any(Income.class), Mockito.any(Income.class));

		IncomeNotFoundException exception = assertThrows(
				IncomeNotFoundException.class,
				() -> incomeService.updateIncome(IncomeTestConstants.NonExistent.ID, incomeRequest),
				errorMessage
		);
		
		assertEquals(errorMessage, exception.getMessage());
		Mockito.verify(incomeBudgetSynchronizer, Mockito.never())
				.reapply(Mockito.any(Income.class), Mockito.any(Income.class));
		Mockito.verify(incomeRepository, Mockito.never())
				.saveAndFlush(Mockito.any(Income.class));
	}
	
	@Test
	void deleteIncome_NonExistentId_ReturnsNotFound() {
		String errorMessage = String.format(TestMessages.IncomeErrorMessageConstants.INCOME_NOT_FOUND_WITH_ID, IncomeTestConstants.NonExistent.ID);
		Mockito.when(incomeRepository.findById(IncomeTestConstants.NonExistent.ID))
				.thenReturn(Optional.empty());
		Mockito.doNothing().when(incomeBudgetSynchronizer)
				.retract(Mockito.any(Income.class));
		
		IncomeNotFoundException exception = assertThrows(
				IncomeNotFoundException.class,
				() -> incomeService.deleteIncome(IncomeTestConstants.NonExistent.ID),
				errorMessage
		);
		
		assertEquals(errorMessage, exception.getMessage());
		Mockito.verify(incomeBudgetSynchronizer, Mockito.never())
				.retract(Mockito.any(Income.class));
		Mockito.verify(incomeRepository, Mockito.never())
				.deleteById(Mockito.anyLong());
	}
}