package com.budgetmaster.controller;

import com.budgetmaster.constants.api.ApiMessages;
import com.budgetmaster.constants.api.ApiPaths;
import com.budgetmaster.constants.validation.ValidationPatterns;
import com.budgetmaster.dto.ExpenseRequest;
import com.budgetmaster.model.Expense;
import com.budgetmaster.service.ExpenseService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

@RestController
@RequestMapping(ApiPaths.Expenses.ROOT)
@Validated
public class ExpenseController {
	
	private final ExpenseService expenseService;
	
	public ExpenseController(ExpenseService expenseService) {
		this.expenseService = expenseService;
	}
	
	@PostMapping
	public ResponseEntity<Expense> createExpense(@Valid @RequestBody ExpenseRequest request) {
 		Expense expense = expenseService.createExpense(request);
		return ResponseEntity.ok(expense);
	}
    
    @GetMapping
    public ResponseEntity<List<Expense>> getAllExpensesForMonth(
    		@RequestParam 
    		@Pattern(regexp = ValidationPatterns.VALIDATION_PATTERN_YEAR_MONTH_REGEX, message = ApiMessages.ValidationMessages.MONTH_FORMAT_INVALID) 
    		String month) {
        List<Expense> expenses = expenseService.getAllExpensesForMonth(month);
        return ResponseEntity.ok(expenses);
    }
    
    @GetMapping(ApiPaths.Expenses.BY_ID)
    public ResponseEntity<Expense> getExpenseById(@PathVariable Long id) {
        Expense expense = expenseService.getExpenseById(id);
        return ResponseEntity.ok(expense);
    }
	
    @PutMapping(ApiPaths.Expenses.BY_ID)
    public ResponseEntity<Expense> updateExpense(@PathVariable Long id, @Valid @RequestBody ExpenseRequest request) {
        Expense expense = expenseService.updateExpense(id, request);
        return ResponseEntity.ok(expense);
    }
	
    @DeleteMapping(ApiPaths.Expenses.BY_ID)
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
	}
}