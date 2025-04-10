package com.budgetmaster.utils.service;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.data.jpa.repository.JpaRepository;

public class ServiceUtils {
    
    /**
     * Finds an entity by its ID. If the entity is not found, throws the provided exception.
     * 
     * Example usage:
     * Budget budget = findByIdOrThrow(budgetRepository, id, () -> new BudgetNotFoundException("Budget not found"));
     * 
     * @param repository The JpaRepository instance to search in
     * @param id The ID of the entity to find
     * @param exceptionSupplier Function that creates the exception to throw if entity is not found
     */
    public static <T, R extends JpaRepository<T, Long>, E extends RuntimeException> T findByIdOrThrow(
            R repository, 
            Long id, 
            Supplier<E> exceptionSupplier) {
        Optional<T> entity = repository.findById(id);
        if (entity.isEmpty()) {
            throw exceptionSupplier.get();
        }
        return entity.get();
    }
    
    /**
     * Finds an entity using a custom finder method. If the entity is not found, throws the provided exception.
     * 
     * Example usage:
     * Budget budget = findByCustomFinderOrThrow(
     *     budgetRepository::findByMonthYear,
     *     monthYear,
     *     () -> new BudgetNotFoundException("Budget not found for month: " + monthYear)
     * );
     * 
     * @param finderFunction The repository method to use for finding the entity
     * @param parameter The parameter to pass to the finder function
     * @param exceptionSupplier Function that creates the exception to throw if entity is not found
     */
    public static <T, P, E extends RuntimeException> T findByCustomFinderOrThrow(
            Function<P, Optional<T>> finderFunction,
            P parameter,
            Supplier<E> exceptionSupplier) {
        Optional<T> entity = finderFunction.apply(parameter);
        if (entity.isEmpty()) {
            throw exceptionSupplier.get();
        }
        return entity.get();
    }
} 