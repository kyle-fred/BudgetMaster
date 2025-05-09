package com.budgetmaster.common.service;

import java.time.YearMonth;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import com.budgetmaster.budget.exception.BudgetNotFoundException;
import com.budgetmaster.budget.model.Budget;
import com.budgetmaster.income.exception.IncomeNotFoundException;
import com.budgetmaster.income.model.Income;
import com.budgetmaster.test.builder.BudgetTestBuilder;
import com.budgetmaster.test.constants.TestMessages;
import com.budgetmaster.test.constants.TestData.BudgetTestConstants;
import com.budgetmaster.test.factory.BudgetTestFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.repository.JpaRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EntityLookupServiceTest extends EntityLookupService {
	// -- Dependencies --
	@Mock
	private JpaRepository<Budget, Long> mockRepository;

	// -- Test Objects --
	private EntityLookupService entityLookupService;

	// -- Test Objects --
	private Budget testBudget;
	
	@Mock
	private Income testIncome;
	
	@BeforeEach
	void setUp() {
		entityLookupService = new EntityLookupService();
		testBudget = BudgetTestFactory.createDefaultBudget();
	}
	
	@Test
	void findByIdOrThrow_WhenEntityExists_ReturnsEntity() {
		when(mockRepository.findById(BudgetTestConstants.Default.ID))
				.thenReturn(Optional.of(testBudget));
		
		Budget result = entityLookupService.findByIdOrThrow(
				mockRepository,
				BudgetTestConstants.Default.ID,
				() -> new BudgetNotFoundException(TestMessages.BudgetErrorMessageConstants.BUDGET_NOT_FOUND_WITH_ID)
		);
		
		assertEquals(testBudget, result);
		verify(mockRepository)
				.findById(BudgetTestConstants.Default.ID);
	}
	
	@Test
	void findByIdOrThrow_WhenEntityDoesNotExist_ThrowsException() {
		when(mockRepository.findById(BudgetTestConstants.Default.ID))
				.thenReturn(Optional.empty());
		String errorMessage = String.format(TestMessages.BudgetErrorMessageConstants.BUDGET_NOT_FOUND_WITH_ID, BudgetTestConstants.Default.ID);
		
		BudgetNotFoundException exception = assertThrows(
				BudgetNotFoundException.class,
				() -> entityLookupService.findByIdOrThrow(
						mockRepository,
						BudgetTestConstants.Default.ID,
						() -> new BudgetNotFoundException(errorMessage)
				)
		);
		
		assertEquals(errorMessage, exception.getMessage());
		verify(mockRepository)
				.findById(BudgetTestConstants.Default.ID);
	}
	
	@Test
	void findByCustomFinderOrThrow_WhenEntityExists_ReturnsEntity() {
		Function<YearMonth, Optional<Budget>> finder = month -> Optional.of(testBudget);
		
		Budget result = entityLookupService.findByCustomFinderOrThrow(
				finder,
				BudgetTestConstants.Default.YEAR_MONTH,
				() -> new BudgetNotFoundException(String.format(TestMessages.BudgetErrorMessageConstants.BUDGET_NOT_FOUND_FOR_MONTH, BudgetTestConstants.Default.YEAR_MONTH))
		);
		
		assertEquals(testBudget, result);
	}
	
	@Test
	void findByCustomFinderOrThrow_WhenEntityDoesNotExist_ThrowsException() {
		Function<YearMonth, Optional<Budget>> finder = month -> Optional.empty();
		String errorMessage = String.format(TestMessages.BudgetErrorMessageConstants.BUDGET_NOT_FOUND_FOR_MONTH, BudgetTestConstants.Default.YEAR_MONTH);
		
		BudgetNotFoundException exception = assertThrows(
				BudgetNotFoundException.class,
				() -> entityLookupService.findByCustomFinderOrThrow(
						finder,
						BudgetTestConstants.Default.YEAR_MONTH,
						() -> new BudgetNotFoundException(errorMessage)
				)
		);
		
		assertEquals(errorMessage, exception.getMessage());
	}

	@Test
	void findListByCustomFinderOrThrow_WhenEntitiesExists_ReturnsListOfEntities() {
		Function<YearMonth, List<Income>> finder = month -> List.of(testIncome);
		
		List<Income> result = entityLookupService.findListByCustomFinderOrThrow(
				finder,
				BudgetTestConstants.Default.YEAR_MONTH,
				() -> new IncomeNotFoundException(String.format(TestMessages.IncomeErrorMessageConstants.INCOME_NOT_FOUND_BY_MONTH, BudgetTestConstants.Default.YEAR_MONTH))
		);
		
		assertEquals(List.of(testIncome), result);
	}

	@Test
	void findListByCustomFinderOrThrow_WhenEntitiesDoNotExist_ThrowsException() {
		Function<YearMonth, List<Income>> finder = month -> List.of();
		String errorMessage = String.format(TestMessages.IncomeErrorMessageConstants.INCOME_NOT_FOUND_BY_MONTH, BudgetTestConstants.Default.YEAR_MONTH);

		IncomeNotFoundException exception = assertThrows(
				IncomeNotFoundException.class,
				() -> entityLookupService.findListByCustomFinderOrThrow(
						finder, 
						BudgetTestConstants.Default.YEAR_MONTH, 
						() -> new IncomeNotFoundException(errorMessage)
				)
		);
		
		assertEquals(errorMessage, exception.getMessage());
	}
} 