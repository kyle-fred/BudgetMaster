package com.budgetmaster.utils.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.YearMonth;
import java.util.Optional;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.repository.JpaRepository;

import com.budgetmaster.exception.BudgetNotFoundException;
import com.budgetmaster.model.Budget;

@ExtendWith(MockitoExtension.class)
public class ServiceUtilsTest {
    
    @Mock
    private JpaRepository<Budget, Long> mockRepository;
    
    private Budget testBudget;
    private Long testId;
    private YearMonth testMonthYear;
    
    @BeforeEach
    void setUp() {
        testBudget = new Budget();
        testId = 1L;
        testMonthYear = YearMonth.of(2024, 3);
    }
    
    @Test
    void findByIdOrThrow_WhenEntityExists_ReturnsEntity() {
        // Arrange
        when(mockRepository.findById(testId))
                .thenReturn(Optional.of(testBudget));
        
        // Act
        Budget result = ServiceUtils.findByIdOrThrow(
                mockRepository,
                testId,
                () -> new BudgetNotFoundException("Budget not found for id: " + testId)
        );
        
        // Assert
        assertEquals(testBudget, result);
        verify(mockRepository)
                .findById(testId);
    }
    
    @Test
    void findByIdOrThrow_WhenEntityDoesNotExist_ThrowsException() {
        // Arrange
        when(mockRepository.findById(testId))
                .thenReturn(Optional.empty());
        String errorMessage = "Budget not found for id: " + testId;
        
        // Act & Assert
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
        // Arrange
        Function<YearMonth, Optional<Budget>> finder = monthYear -> Optional.of(testBudget);
        
        // Act
        Budget result = ServiceUtils.findByCustomFinderOrThrow(
                finder,
                testMonthYear,
                () -> new BudgetNotFoundException("Budget not found for month: " + testMonthYear)
        );
        
        // Assert
        assertEquals(testBudget, result);
    }
    
    @Test
    void findByCustomFinderOrThrow_WhenEntityDoesNotExist_ThrowsException() {
        // Arrange
        Function<YearMonth, Optional<Budget>> finder = testMonthYear -> Optional.empty();
        String errorMessage = "Budget not found for month: " + testMonthYear;
        
        // Act & Assert
        BudgetNotFoundException exception = assertThrows(
                BudgetNotFoundException.class,
                () -> ServiceUtils.findByCustomFinderOrThrow(
                        finder,
                        testMonthYear,
                        () -> new BudgetNotFoundException(errorMessage)
                )
        );
        
        assertEquals(errorMessage, exception.getMessage());
    }
} 