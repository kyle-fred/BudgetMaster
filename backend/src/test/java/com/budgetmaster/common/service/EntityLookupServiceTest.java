package com.budgetmaster.common.service;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import com.budgetmaster.budget.exception.BudgetNotFoundException;
import com.budgetmaster.budget.model.Budget;
import com.budgetmaster.income.exception.IncomeNotFoundException;
import com.budgetmaster.income.model.Income;
import com.budgetmaster.testsupport.budget.constants.BudgetConstants;
import com.budgetmaster.testsupport.budget.factory.BudgetFactory;
import com.budgetmaster.testsupport.constants.Messages;

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
		testBudget = BudgetFactory.createDefaultBudget();
	}
	
	@Test
	void findByIdOrThrow_WhenEntityExists_ReturnsEntity() {
		when(mockRepository.findById(BudgetConstants.Default.ID))
				.thenReturn(Optional.of(testBudget));
		
		Budget result = entityLookupService.findByIdOrThrow(
				mockRepository,
				BudgetConstants.Default.ID,
				() -> new BudgetNotFoundException(Messages.BudgetErrorMessageConstants.BUDGET_NOT_FOUND_WITH_ID)
		);
		
		assertEquals(testBudget, result);
		verify(mockRepository)
				.findById(BudgetConstants.Default.ID);
	}
	
	@Test
	void findByIdOrThrow_WhenEntityDoesNotExist_ThrowsException() {
		when(mockRepository.findById(BudgetConstants.Default.ID))
				.thenReturn(Optional.empty());
		String errorMessage = String.format(Messages.BudgetErrorMessageConstants.BUDGET_NOT_FOUND_WITH_ID, BudgetConstants.Default.ID);
		
		BudgetNotFoundException exception = assertThrows(
				BudgetNotFoundException.class,
				() -> entityLookupService.findByIdOrThrow(
						mockRepository,
						BudgetConstants.Default.ID,
						() -> new BudgetNotFoundException(errorMessage)
				)
		);
		
		assertEquals(errorMessage, exception.getMessage());
		verify(mockRepository)
				.findById(BudgetConstants.Default.ID);
	}
	
	@Test
	void findByCustomFinderOrThrow_WhenEntityExists_ReturnsEntity() {
		Function<YearMonth, Optional<Budget>> finder = month -> Optional.of(testBudget);
		
		Budget result = entityLookupService.findByCustomFinderOrThrow(
				finder,
				BudgetConstants.Default.YEAR_MONTH,
				() -> new BudgetNotFoundException(String.format(Messages.BudgetErrorMessageConstants.BUDGET_NOT_FOUND_FOR_MONTH, BudgetConstants.Default.YEAR_MONTH))
		);
		
		assertEquals(testBudget, result);
	}
	
	@Test
	void findByCustomFinderOrThrow_WhenEntityDoesNotExist_ThrowsException() {
		Function<YearMonth, Optional<Budget>> finder = month -> Optional.empty();
		String errorMessage = String.format(Messages.BudgetErrorMessageConstants.BUDGET_NOT_FOUND_FOR_MONTH, BudgetConstants.Default.YEAR_MONTH);
		
		BudgetNotFoundException exception = assertThrows(
				BudgetNotFoundException.class,
				() -> entityLookupService.findByCustomFinderOrThrow(
						finder,
						BudgetConstants.Default.YEAR_MONTH,
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
				BudgetConstants.Default.YEAR_MONTH,
				() -> new IncomeNotFoundException(String.format(Messages.IncomeErrorMessageConstants.INCOME_NOT_FOUND_BY_MONTH, BudgetConstants.Default.YEAR_MONTH))
		);
		
		assertEquals(List.of(testIncome), result);
	}

	@Test
	void findListByCustomFinderOrThrow_WhenEntitiesDoNotExist_ThrowsException() {
		Function<YearMonth, List<Income>> finder = month -> List.of();
		String errorMessage = String.format(Messages.IncomeErrorMessageConstants.INCOME_NOT_FOUND_BY_MONTH, BudgetConstants.Default.YEAR_MONTH);

		IncomeNotFoundException exception = assertThrows(
				IncomeNotFoundException.class,
				() -> entityLookupService.findListByCustomFinderOrThrow(
						finder, 
						BudgetConstants.Default.YEAR_MONTH, 
						() -> new IncomeNotFoundException(errorMessage)
				)
		);
		
		assertEquals(errorMessage, exception.getMessage());
	}
} 