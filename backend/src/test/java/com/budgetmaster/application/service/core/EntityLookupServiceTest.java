package com.budgetmaster.application.service.core;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import com.budgetmaster.application.model.Budget;
import com.budgetmaster.application.exception.BudgetNotFoundException;
import com.budgetmaster.application.exception.IncomeNotFoundException;
import com.budgetmaster.application.model.Income;
import com.budgetmaster.testsupport.assertions.model.BudgetModelAssertions;
import com.budgetmaster.testsupport.assertions.model.list.IncomeListAssertions;
import com.budgetmaster.testsupport.builder.model.BudgetBuilder;
import com.budgetmaster.testsupport.builder.model.IncomeBuilder;
import com.budgetmaster.testsupport.constants.ErrorConstants;
import com.budgetmaster.testsupport.constants.domain.BudgetConstants;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.repository.JpaRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Entity Lookup Service Tests")
class EntityLookupServiceTest extends EntityLookupService {

	@Mock
	private JpaRepository<Budget, Long> mockRepository;

	@Mock
	private Income defaultIncome;

	private Budget defaultBudget;
	
	@BeforeEach
	void setUp() {
		defaultBudget = BudgetBuilder.defaultBudget().build();
		defaultIncome = IncomeBuilder.defaultIncome().build();
	}

	@Nested
	@DisplayName("Find By ID Operations")
	class FindByIdOperations {
		
		@Test
		@DisplayName("Should return entity when found by ID")
		void findByIdOrThrow_withExistingEntity_returnsEntity() {
			when(mockRepository.findById(BudgetConstants.Default.ID))
					.thenReturn(Optional.of(defaultBudget));
			
			Budget result = findByIdOrThrow(
					mockRepository,
					BudgetConstants.Default.ID,
					() -> new BudgetNotFoundException(ErrorConstants.Budget.NOT_FOUND_WITH_ID)
			);
			
			BudgetModelAssertions.assertBudget(result)
				.isDefaultBudget();

			verify(mockRepository)
					.findById(BudgetConstants.Default.ID);
		}
		
		@Test
		@DisplayName("Should throw exception when entity not found by ID")
		void findByIdOrThrow_withNonExistentEntity_throwsException() {
			String errorMessage = String.format(ErrorConstants.Budget.NOT_FOUND_WITH_ID, BudgetConstants.Default.ID);

			when(mockRepository.findById(BudgetConstants.Default.ID))
					.thenReturn(Optional.empty());
			
			BudgetNotFoundException exception = assertThrows(
					BudgetNotFoundException.class,
					() -> findByIdOrThrow(
							mockRepository,
							BudgetConstants.Default.ID,
							() -> new BudgetNotFoundException(errorMessage)
					)
			);
			
			assertEquals(errorMessage, exception.getMessage());

			verify(mockRepository)
					.findById(BudgetConstants.Default.ID);
		}
	}

	@Nested
	@DisplayName("Find By Custom Finder Operations")
	class FindByCustomFinderOperations {
		
		@Test
		@DisplayName("Should return entity when found by custom finder")
		void findByCustomFinderOrThrow_withExistingEntity_returnsEntity() {
			Function<YearMonth, Optional<Budget>> finder = month -> Optional.of(defaultBudget);
			
			Budget result = findByCustomFinderOrThrow(
					finder,
					BudgetConstants.Default.YEAR_MONTH,
					() -> new BudgetNotFoundException(String.format(ErrorConstants.Budget.NOT_FOUND_FOR_MONTH, BudgetConstants.Default.YEAR_MONTH))
			);
			
			BudgetModelAssertions.assertBudget(result)
				.isDefaultBudget();
		}
		
		@Test
		@DisplayName("Should throw exception when entity not found by custom finder")
		void findByCustomFinderOrThrow_withNonExistentEntity_throwsException() {
			Function<YearMonth, Optional<Budget>> finder = month -> Optional.empty();
			String errorMessage = String.format(ErrorConstants.Budget.NOT_FOUND_FOR_MONTH, BudgetConstants.Default.YEAR_MONTH);
			
			BudgetNotFoundException exception = assertThrows(
					BudgetNotFoundException.class,
					() -> findByCustomFinderOrThrow(
							finder,
							BudgetConstants.Default.YEAR_MONTH,
							() -> new BudgetNotFoundException(errorMessage)
					)
			);
			
			assertEquals(errorMessage, exception.getMessage());
		}
	}

	@Nested
	@DisplayName("Find List By Custom Finder Operations")
	class FindListByCustomFinderOperations {
		
		@Test
		@DisplayName("Should return list of entities when found by custom finder")
		void findListByCustomFinderOrThrow_withExistingEntities_returnsListOfEntities() {
			Function<YearMonth, List<Income>> finder = month -> List.of(defaultIncome);
			
			List<Income> result = findListByCustomFinderOrThrow(
					finder,
					BudgetConstants.Default.YEAR_MONTH,
					() -> new IncomeNotFoundException(String.format(ErrorConstants.Income.NOT_FOUND_FOR_MONTH, BudgetConstants.Default.YEAR_MONTH))
			);

			IncomeListAssertions.assertIncomes(result)
				.hasSize(1)
				.first()
				.isDefaultIncome();
		}

		@Test
		@DisplayName("Should throw exception when no entities found by custom finder")
		void findListByCustomFinderOrThrow_withNonExistentEntities_throwsException() {
			Function<YearMonth, List<Income>> finder = month -> List.of();
			String errorMessage = String.format(ErrorConstants.Income.NOT_FOUND_FOR_MONTH, BudgetConstants.Default.YEAR_MONTH);

			IncomeNotFoundException exception = assertThrows(
					IncomeNotFoundException.class,
					() -> findListByCustomFinderOrThrow(
							finder, 
							BudgetConstants.Default.YEAR_MONTH, 
							() -> new IncomeNotFoundException(errorMessage)
					)
			);
			
			assertEquals(errorMessage, exception.getMessage());
		}
	}
} 