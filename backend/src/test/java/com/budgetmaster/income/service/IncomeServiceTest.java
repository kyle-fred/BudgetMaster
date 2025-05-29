package com.budgetmaster.income.service;

import java.util.List;
import java.util.Optional;

import com.budgetmaster.application.dto.IncomeRequest;
import com.budgetmaster.application.model.Income;
import com.budgetmaster.application.repository.IncomeRepository;
import com.budgetmaster.application.service.IncomeService;
import com.budgetmaster.application.service.synchronization.IncomeBudgetSynchronizer;
import com.budgetmaster.application.util.DateUtils;
import com.budgetmaster.common.enums.ErrorCode;
import com.budgetmaster.config.JacksonConfig;
import com.budgetmaster.exception.IncomeNotFoundException;
import com.budgetmaster.testsupport.constants.Error;
import com.budgetmaster.testsupport.income.constants.IncomeConstants;
import com.budgetmaster.testsupport.income.factory.IncomeFactory;

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
		testIncome = IncomeFactory.createDefaultIncome();
		incomeRequest = IncomeFactory.createDefaultIncomeRequest();
	}
	
	@Test
	void createIncome_ValidRequest_ReturnsCreated() {
		Mockito.when(incomeRepository.saveAndFlush(Mockito.any(Income.class)))
				.thenReturn(testIncome);
		Mockito.doNothing().when(incomeBudgetSynchronizer)
				.apply(Mockito.any(Income.class));
		
		Income savedIncome = incomeService.createIncome(incomeRequest);

		assertNotNull(savedIncome);
		assertEquals(IncomeConstants.Default.NAME, savedIncome.getName());
		assertEquals(IncomeConstants.Default.SOURCE, savedIncome.getSource());
		assertEquals(IncomeConstants.Default.AMOUNT, savedIncome.getMoney().getAmount());
		assertEquals(IncomeConstants.Default.CURRENCY, savedIncome.getMoney().getCurrency());
		assertEquals(IncomeConstants.Default.TYPE, savedIncome.getType());
		assertEquals(IncomeConstants.Default.YEAR_MONTH, savedIncome.getMonth());

		Mockito.verify(incomeBudgetSynchronizer, Mockito.times(1))
				.apply(testIncome);
		Mockito.verify(incomeRepository, Mockito.times(1))
				.saveAndFlush(Mockito.any(Income.class));
	}
	
	@Test
	void getAllIncomes_ValidMonth_ReturnsOk() {
		List<Income> incomes = List.of(testIncome, testIncome);
		
		try (MockedStatic<DateUtils> mockedDateUtils = mockStatic(DateUtils.class)) {
			mockedDateUtils.when(() -> DateUtils.getValidYearMonth(IncomeConstants.Default.YEAR_MONTH.toString()))
					.thenReturn(IncomeConstants.Default.YEAR_MONTH);
			Mockito.when(incomeRepository.findByMonth(IncomeConstants.Default.YEAR_MONTH))
					.thenReturn(incomes);

			List<Income> result = incomeService.getAllIncomesForMonth(IncomeConstants.Default.YEAR_MONTH.toString());

			assertNotNull(result);
			assertEquals(2, result.size());
			assertEquals(IncomeConstants.Default.NAME, result.get(0).getName());
			assertEquals(IncomeConstants.Default.NAME, result.get(1).getName());

			Mockito.verify(incomeRepository, Mockito.times(1))
					.findByMonth(IncomeConstants.Default.YEAR_MONTH);
		}
	}
	
	@Test
	void getIncome_ValidId_ReturnsOk() {
		Mockito.when(incomeRepository.findById(IncomeConstants.Default.ID))
				.thenReturn(Optional.of(testIncome));
		
		Income retrievedIncome = incomeService.getIncomeById(IncomeConstants.Default.ID);
		
		assertNotNull(retrievedIncome);
		assertEquals(IncomeConstants.Default.NAME, retrievedIncome.getName());
		assertEquals(IncomeConstants.Default.SOURCE, retrievedIncome.getSource());
		assertEquals(IncomeConstants.Default.AMOUNT, retrievedIncome.getMoney().getAmount());
		assertEquals(IncomeConstants.Default.CURRENCY, retrievedIncome.getMoney().getCurrency());
		assertEquals(IncomeConstants.Default.TYPE, retrievedIncome.getType());
		assertEquals(IncomeConstants.Default.YEAR_MONTH, retrievedIncome.getMonth());
		
		Mockito.verify(incomeRepository, Mockito.times(1))
				.findById(IncomeConstants.Default.ID);
	}
	
	@Test
	void updateIncome_ValidRequest_ReturnsOk() {
		IncomeRequest updatedRequest = IncomeFactory.createUpdatedIncomeRequest();

		Mockito.when(incomeRepository.findById(IncomeConstants.Default.ID))
				.thenReturn(Optional.of(testIncome));
		Mockito.doNothing().when(incomeBudgetSynchronizer)
				.reapply(Mockito.any(Income.class), Mockito.any(Income.class));
        
		Mockito.when(incomeRepository.saveAndFlush(Mockito.any(Income.class)))
				.thenReturn(testIncome);

		Income updatedIncome = incomeService.updateIncome(IncomeConstants.Default.ID, updatedRequest);

		assertNotNull(updatedIncome);
		assertEquals(IncomeConstants.Updated.NAME, updatedIncome.getName());
		assertEquals(IncomeConstants.Updated.SOURCE, updatedIncome.getSource());
		assertEquals(IncomeConstants.Updated.AMOUNT, updatedIncome.getMoney().getAmount());
		assertEquals(IncomeConstants.Default.CURRENCY, updatedIncome.getMoney().getCurrency());
		assertEquals(IncomeConstants.Updated.TYPE, updatedIncome.getType());
		assertEquals(IncomeConstants.Updated.YEAR_MONTH, updatedIncome.getMonth());
		
		Mockito.verify(incomeBudgetSynchronizer, Mockito.times(1))
				.reapply(Mockito.any(Income.class), Mockito.any(Income.class));
		Mockito.verify(incomeRepository, Mockito.times(1))
				.saveAndFlush(Mockito.any(Income.class));
	}
	
	@Test
	void deleteIncome_ValidId_ReturnsNoContent() {
		Mockito.when(incomeRepository.findById(IncomeConstants.Default.ID))
				.thenReturn(Optional.of(testIncome));
		Mockito.doNothing().when(incomeBudgetSynchronizer)
				.retract(Mockito.any(Income.class));
		Mockito.doNothing()
				.when(incomeRepository)
				.deleteById(IncomeConstants.Default.ID);
		
		incomeService.deleteIncome(IncomeConstants.Default.ID);

		Mockito.verify(incomeBudgetSynchronizer, Mockito.times(1))
				.retract(Mockito.any(Income.class));
		Mockito.verify(incomeRepository, Mockito.times(1))
				.findById(IncomeConstants.Default.ID);
		Mockito.verify(incomeRepository, Mockito.times(1))
				.deleteById(IncomeConstants.Default.ID);
	}
	
	@Test
	void createIncome_ServiceError_ReturnsInternalServerError() {
		String errorMessage = ErrorCode.DATABASE_ERROR.getMessage();
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
		String errorMessage = String.format(Error.Income.NOT_FOUND_WITH_ID, IncomeConstants.NonExistent.ID);
		Mockito.when(incomeRepository.findById(IncomeConstants.NonExistent.ID))
				.thenReturn(Optional.empty());
		
		IncomeNotFoundException exception = assertThrows(
				IncomeNotFoundException.class,
				() -> incomeService.getIncomeById(IncomeConstants.NonExistent.ID),
				errorMessage
		);
		
		assertEquals(errorMessage, exception.getMessage());
	}
	
	@Test
	void getAllIncomes_NoIncomes_ReturnsNotFound() {
		String errorMessage = String.format(Error.Income.NOT_FOUND_BY_MONTH, IncomeConstants.NonExistent.YEAR_MONTH);
		
		try (MockedStatic<DateUtils> mockedDateUtils = mockStatic(DateUtils.class)) {
			mockedDateUtils.when(() -> DateUtils.getValidYearMonth(IncomeConstants.NonExistent.YEAR_MONTH.toString()))
					.thenReturn(IncomeConstants.NonExistent.YEAR_MONTH);
			Mockito.when(incomeRepository.findByMonth(IncomeConstants.NonExistent.YEAR_MONTH))
					.thenThrow(new IncomeNotFoundException(errorMessage));

			IncomeNotFoundException exception = assertThrows(
					IncomeNotFoundException.class,
					() -> incomeService.getAllIncomesForMonth(IncomeConstants.NonExistent.YEAR_MONTH.toString()),
					errorMessage
			);
			
			assertEquals(errorMessage, exception.getMessage());
		}
	}
	
	@Test
	void updateIncome_NonExistentId_ReturnsNotFound() {
		String errorMessage = String.format(Error.Income.NOT_FOUND_WITH_ID, IncomeConstants.NonExistent.ID);
		Mockito.when(incomeRepository.findById(IncomeConstants.NonExistent.ID))
				.thenReturn(Optional.empty());
		Mockito.doNothing().when(incomeBudgetSynchronizer)
				.reapply(Mockito.any(Income.class), Mockito.any(Income.class));

		IncomeNotFoundException exception = assertThrows(
				IncomeNotFoundException.class,
				() -> incomeService.updateIncome(IncomeConstants.NonExistent.ID, incomeRequest),
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
		String errorMessage = String.format(Error.Income.NOT_FOUND_WITH_ID, IncomeConstants.NonExistent.ID);
		Mockito.when(incomeRepository.findById(IncomeConstants.NonExistent.ID))
				.thenReturn(Optional.empty());
		Mockito.doNothing().when(incomeBudgetSynchronizer)
				.retract(Mockito.any(Income.class));
		
		IncomeNotFoundException exception = assertThrows(
				IncomeNotFoundException.class,
				() -> incomeService.deleteIncome(IncomeConstants.NonExistent.ID),
				errorMessage
		);
		
		assertEquals(errorMessage, exception.getMessage());
		Mockito.verify(incomeBudgetSynchronizer, Mockito.never())
				.retract(Mockito.any(Income.class));
		Mockito.verify(incomeRepository, Mockito.never())
				.deleteById(Mockito.anyLong());
	}
}