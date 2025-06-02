package com.budgetmaster.application.service;

import java.util.List;
import java.util.Optional;

import com.budgetmaster.application.dto.IncomeRequest;
import com.budgetmaster.application.exception.IncomeNotFoundException;
import com.budgetmaster.application.exception.codes.ErrorCode;
import com.budgetmaster.application.model.Income;
import com.budgetmaster.application.repository.IncomeRepository;
import com.budgetmaster.application.service.synchronization.IncomeBudgetSynchronizer;
import com.budgetmaster.application.util.DateUtils;
import com.budgetmaster.config.JacksonConfig;
import com.budgetmaster.testsupport.assertions.model.IncomeModelAssertions;
import com.budgetmaster.testsupport.assertions.model.list.IncomeListAssertions;
import com.budgetmaster.testsupport.builder.dto.IncomeRequestBuilder;
import com.budgetmaster.testsupport.builder.model.IncomeBuilder;
import com.budgetmaster.testsupport.constants.ErrorConstants;
import com.budgetmaster.testsupport.constants.domain.IncomeConstants;

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

	private final IncomeRepository incomeRepository = mock(IncomeRepository.class);
	private final IncomeBudgetSynchronizer incomeBudgetSynchronizer = mock(IncomeBudgetSynchronizer.class);
	private final IncomeService incomeService = new IncomeService(incomeRepository, incomeBudgetSynchronizer);
	
	private Income testIncome;
	private IncomeRequest incomeRequest;
	
	@BeforeEach
	void setUp() {
		testIncome = IncomeBuilder.defaultIncome().build();
		incomeRequest = IncomeRequestBuilder.defaultIncomeRequest().buildRequest();
	}
	
	@Test
	void createIncome_ValidRequest_ReturnsCreated() {
		Mockito.when(incomeRepository.saveAndFlush(Mockito.any(Income.class)))
				.thenReturn(testIncome);
		Mockito.doNothing().when(incomeBudgetSynchronizer)
				.apply(Mockito.any(Income.class));
		
		Income savedIncome = incomeService.createIncome(incomeRequest);

		IncomeModelAssertions.assertIncome(savedIncome)
			.isDefaultIncome();

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

			IncomeListAssertions.assertIncomes(result)
				.hasSize(2)
				.first()
				.isDefaultIncome();

			Mockito.verify(incomeRepository, Mockito.times(1))
					.findByMonth(IncomeConstants.Default.YEAR_MONTH);
		}
	}
	
	@Test
	void getIncome_ValidId_ReturnsOk() {
		Mockito.when(incomeRepository.findById(IncomeConstants.Default.ID))
				.thenReturn(Optional.of(testIncome));
		
		Income retrievedIncome = incomeService.getIncomeById(IncomeConstants.Default.ID);
		
		IncomeModelAssertions.assertIncome(retrievedIncome);
		
		Mockito.verify(incomeRepository, Mockito.times(1))
				.findById(IncomeConstants.Default.ID);
	}
	
	@Test
	void updateIncome_ValidRequest_ReturnsOk() {
		IncomeRequest updatedRequest = IncomeRequestBuilder.updatedIncomeRequest().buildRequest();

		Mockito.when(incomeRepository.findById(IncomeConstants.Default.ID))
				.thenReturn(Optional.of(testIncome));
		Mockito.doNothing().when(incomeBudgetSynchronizer)
				.reapply(Mockito.any(Income.class), Mockito.any(Income.class));
		Mockito.when(incomeRepository.saveAndFlush(Mockito.any(Income.class)))
				.thenReturn(testIncome);

		Income updatedIncome = incomeService.updateIncome(IncomeConstants.Default.ID, updatedRequest);

		IncomeModelAssertions.assertIncome(updatedIncome)
			.isUpdatedIncome();
		
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
		String errorMessage = String.format(ErrorConstants.Income.NOT_FOUND_WITH_ID, IncomeConstants.NonExistent.ID);
		
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
		String errorMessage = String.format(ErrorConstants.Income.NOT_FOUND_BY_MONTH, IncomeConstants.NonExistent.YEAR_MONTH);
		
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
		String errorMessage = String.format(ErrorConstants.Income.NOT_FOUND_WITH_ID, IncomeConstants.NonExistent.ID);
		
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
		String errorMessage = String.format(ErrorConstants.Income.NOT_FOUND_WITH_ID, IncomeConstants.NonExistent.ID);
		
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