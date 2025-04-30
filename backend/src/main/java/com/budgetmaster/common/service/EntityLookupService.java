package com.budgetmaster.common.service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.data.jpa.repository.JpaRepository;

public class EntityLookupService {
    
    protected <T, E extends RuntimeException> T findByIdOrThrow(
            JpaRepository<T, Long> repository,
            Long id,
            Supplier<E> exceptionSupplier) {
        return repository.findById(id).orElseThrow(exceptionSupplier);
    }

    protected <T, P, E extends RuntimeException> T findByCustomFinderOrThrow(
            Function<P, Optional<T>> finderFunction,
            P parameter,
            Supplier<E> exceptionSupplier) {
        return finderFunction.apply(parameter).orElseThrow(exceptionSupplier);
    }

    protected <T, P, E extends RuntimeException> List<T> findListByCustomFinderOrThrow(
            Function<P, List<T>> finderFunction,
            P parameter,
            Supplier<E> exceptionSupplier) {
        List<T> entities = finderFunction.apply(parameter);
        if (entities.isEmpty()) {
            throw exceptionSupplier.get();
        }
        return entities;
    }
}
