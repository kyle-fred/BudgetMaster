package com.budgetmaster.utils.service;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import com.budgetmaster.exception.BudgetNotFoundException;
import com.budgetmaster.exception.IncomeNotFoundException;
import com.budgetmaster.model.Budget;
import com.budgetmaster.model.Income;
import com.budgetmaster.test.constants.TestData;
import com.budgetmaster.test.constants.TestMessages;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.repository.JpaRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServiceUtilsTest {
	// -- Dependencies --
	@Mock
	private JpaRepository<Budget, Long> mockRepository;
	
	// -- Test Data --
	private static final Long testId = TestData.CommonTestDataConstants.ID_EXISTING;
	private static final YearMonth testMonth = TestData.MonthTestDataConstants.MONTH_EXISTING;
	
	// -- Test Objects --
	private Budget testBudget;
	private Income testIncome;
	
	// -- Setup --
	@BeforeEach
	void setUp() {
		testBudget = new Budget(testMonth);
		testIncome = new Income();
	}
	
	// -- Find By Id Tests --
	@Test
	void findByIdOrThrow_WhenEntityExists_ReturnsEntity() {
		when(mockRepository.findById(testId))
				.thenReturn(Optional.of(testBudget));
		
		Budget result = ServiceUtils.findByIdOrThrow(
				mockRepository,
				testId,
				() -> new BudgetNotFoundException(TestMessages.BudgetErrorMessageConstants.BUDGET_NOT_FOUND_WITH_ID)
		);
		
		assertEquals(testBudget, result);
		verify(mockRepository)
				.findById(testId);
	}
	
	@Test
	void findByIdOrThrow_WhenEntityDoesNotExist_ThrowsException() {
		when(mockRepository.findById(testId))
				.thenReturn(Optional.empty());
		String errorMessage = String.format(TestMessages.BudgetErrorMessageConstants.BUDGET_NOT_FOUND_WITH_ID, testId);
		
		BudgetNotFoundException exception = assertThrows(
				BudgetNotFoundException.class,
				() -> ServiceUtils.findByIdOrThrow(
						mockRepository,
						testId,
						() -> new BudgetNotFoundException(errorMessage)
				)
		);
		
		assertEquals(errorMessage, exception.getMessage());
		verify(mockRepository)
				.findById(testId);
	}
	
	// -- Find By Custom Finder Tests --
	@Test
	void findByCustomFinderOrThrow_WhenEntityExists_ReturnsEntity() {
		Function<YearMonth, Optional<Budget>> finder = month -> Optional.of(testBudget);
		
		Budget result = ServiceUtils.findByCustomFinderOrThrow(
				finder,
				testMonth,
				() -> new BudgetNotFoundException(String.format(TestMessages.BudgetErrorMessageConstants.BUDGET_NOT_FOUND_FOR_MONTH, testMonth))
		);
		
		assertEquals(testBudget, result);
	}
	
	@Test
	void findByCustomFinderOrThrow_WhenEntityDoesNotExist_ThrowsException() {
		Function<YearMonth, Optional<Budget>> finder = month -> Optional.empty();
		String errorMessage = String.format(TestMessages.BudgetErrorMessageConstants.BUDGET_NOT_FOUND_FOR_MONTH, testMonth);
		
		BudgetNotFoundException exception = assertThrows(
				BudgetNotFoundException.class,
				() -> ServiceUtils.findByCustomFinderOrThrow(
						finder,
						testMonth,
						() -> new BudgetNotFoundException(errorMessage)
				)
		);
		
		assertEquals(errorMessage, exception.getMessage());
	}

	// -- Find List By Custom Finder Tests --
	@Test
	void findListByCustomFinderOrThrow_WhenEntitiesExists_ReturnsListOfEntities() {
		Function<YearMonth, List<Income>> finder = month -> List.of(testIncome);
		
		List<Income> result = ServiceUtils.findListByCustomFinderOrThrow(
				finder,
				testMonth,
				() -> new IncomeNotFoundException(String.format(TestMessages.IncomeErrorMessageConstants.INCOME_NOT_FOUND_BY_MONTH, testMonth))
		);
		
		assertEquals(List.of(testIncome), result);
	}

	@Test
	void findListByCustomFinderOrThrow_WhenEntitiesDoNotExist_ThrowsException() {
		Function<YearMonth, List<Income>> finder = month -> List.of();
		String errorMessage = String.format(TestMessages.IncomeErrorMessageConstants.INCOME_NOT_FOUND_BY_MONTH, testMonth);

		IncomeNotFoundException exception = assertThrows(
				IncomeNotFoundException.class,
				() -> ServiceUtils.findListByCustomFinderOrThrow(
						finder, 
						testMonth, 
						() -> new IncomeNotFoundException(errorMessage)
				)
		);
		
		assertEquals(errorMessage, exception.getMessage());
	}
} 