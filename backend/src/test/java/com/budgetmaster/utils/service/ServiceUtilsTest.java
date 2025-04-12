package com.budgetmaster.utils.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.repository.JpaRepository;

import com.budgetmaster.exception.BudgetNotFoundException;
import com.budgetmaster.exception.IncomeNotFoundException;
import com.budgetmaster.model.Budget;
import com.budgetmaster.model.Income;

@ExtendWith(MockitoExtension.class)
public class ServiceUtilsTest {
    
    @Mock
    private JpaRepository<Budget, Long> mockRepository;
    
    private Budget testBudget;
    private Income testIncome;
    private Long testId;
    private YearMonth testMonth;
    
    @BeforeEach
    void setUp() {
        testMonth = YearMonth.of(2024, 3);
        testBudget = new Budget(testMonth);
        testIncome = new Income();
        testId = 1L;
    }
    
    @Test
    void findByIdOrThrow_WhenEntityExists_ReturnsEntity() {
        when(mockRepository.findById(testId))
                .thenReturn(Optional.of(testBudget));
        
        Budget result = ServiceUtils.findByIdOrThrow(
                mockRepository,
                testId,
                () -> new BudgetNotFoundException("Budget not found for id: " + testId)
        );
        
        assertEquals(testBudget, result);
        verify(mockRepository)
                .findById(testId);
    }
    
    @Test
    void findByIdOrThrow_WhenEntityDoesNotExist_ThrowsException() {
        when(mockRepository.findById(testId))
                .thenReturn(Optional.empty());
        String errorMessage = "Budget not found for id: " + testId;
        
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
    
    @Test
    void findByCustomFinderOrThrow_WhenEntityExists_ReturnsEntity() {
        Function<YearMonth, Optional<Budget>> finder = month -> Optional.of(testBudget);
        
        Budget result = ServiceUtils.findByCustomFinderOrThrow(
                finder,
                testMonth,
                () -> new BudgetNotFoundException("Budget not found for month: " + testMonth)
        );
        
        assertEquals(testBudget, result);
    }
    
    @Test
    void findByCustomFinderOrThrow_WhenEntityDoesNotExist_ThrowsException() {
        Function<YearMonth, Optional<Budget>> finder = month -> Optional.empty();
        String errorMessage = "Budget not found for month: " + testMonth;
        
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

    @Test
    void findListByCustomFinderOrThrow_WhenEntitiesExists_ReturnsListOfEntities() {
        Function<YearMonth, List<Income>> finder = month -> List.of(testIncome);
        
        List<Income> result = ServiceUtils.findListByCustomFinderOrThrow(
                finder,
                testMonth,
                () -> new IncomeNotFoundException("Incomes not found for month: " + testMonth)
        );
        
        assertEquals(List.of(testIncome), result);
    }

    @Test
    void findListByCustomFinderOrThrow_WhenEntitiesDoNotExist_ThrowsException() {
        Function<YearMonth, List<Income>> finder = month -> List.of();
        String errorMessage = "Incomes not found for month: " + testMonth;

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