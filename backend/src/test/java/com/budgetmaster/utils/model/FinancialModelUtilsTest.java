package com.budgetmaster.utils.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.YearMonth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.budgetmaster.dto.IncomeRequest;
import com.budgetmaster.dto.ExpenseRequest;
import com.budgetmaster.enums.ExpenseCategory;
import com.budgetmaster.enums.TransactionType;
import com.budgetmaster.model.Income;
import com.budgetmaster.model.Expense;

public class FinancialModelUtilsTest {

    private IncomeRequest incomeRequest;
    private ExpenseRequest expenseRequest;

    @BeforeEach
    void setUp() {
        incomeRequest = new IncomeRequest();
        incomeRequest.setName("Salary");
        incomeRequest.setSource("Company XYZ");
        incomeRequest.setAmount(2000.0);
        incomeRequest.setType(TransactionType.RECURRING);
        incomeRequest.setMonthYear("2000-01");
        
        expenseRequest = new ExpenseRequest();
        expenseRequest.setName("Rent");
        expenseRequest.setAmount(1000.0);
        expenseRequest.setCategory(ExpenseCategory.HOUSING);
        expenseRequest.setType(TransactionType.RECURRING);
        expenseRequest.setMonthYear("2000-01");
    }

    // ----- Income Tests -----
    @Test
    void testBuildIncome_CreatesIncomeWithValidValues() {
        Income income = FinancialModelUtils.buildIncome(incomeRequest);
        
        assertNotNull(income, "Income object should not be null");
        assertEquals("SALARY", income.getName(), "Name should be capitalized");
        assertEquals("COMPANY XYZ", income.getSource(), "Source should be capitalized");
        assertEquals(2000.0, income.getAmount(), "Amount should match");
        assertEquals(TransactionType.RECURRING, income.getType(), "Transaction type should match");
        assertEquals(YearMonth.of(2000, 1), income.getMonthYear(), "MonthYear should be correctly set");
    }
    
    @Test
    void testModifyIncome_ValidRequest_ModifiesIncomeCorrectly() {
        YearMonth testMonthYear = YearMonth.of(2000, 1);
        Income income = new Income("Salary", "Company XYZ", 2000.0, TransactionType.RECURRING, testMonthYear);
        
        IncomeRequest updateRequest = new IncomeRequest();
        updateRequest.setName("Bonus");
        updateRequest.setSource("Company ABC");
        updateRequest.setAmount(3000.0);
        updateRequest.setType(TransactionType.ONE_TIME);
        updateRequest.setMonthYear("2000-01");

        FinancialModelUtils.modifyIncome(income, updateRequest);
        
        assertEquals("BONUS", income.getName(), "Name should be capitalized");
        assertEquals("COMPANY ABC", income.getSource(), "Source should be capitalized");
        assertEquals(3000.0, income.getAmount(), "Amount should match");
        assertEquals(TransactionType.ONE_TIME, income.getType(), "Transaction type should match");
        assertEquals(YearMonth.of(2000, 1), income.getMonthYear(), "MonthYear should be correctly set");
    }

    // ----- Expense Tests -----
    @Test
    void testBuildExpense_CreatesExpenseWithValidValues() {
        Expense expense = FinancialModelUtils.buildExpense(expenseRequest);
        
        assertNotNull(expense, "Expense object should not be null");
        assertEquals("RENT", expense.getName(), "Name should be capitalized");
        assertEquals(1000.0, expense.getAmount(), "Amount should match");
        assertEquals(ExpenseCategory.HOUSING, expense.getCategory(), "Expense category should match");
        assertEquals(TransactionType.RECURRING, expense.getType(), "Transaction type should match");
        assertEquals(YearMonth.of(2000, 1), expense.getMonthYear(), "MonthYear should be correctly set");
    }
    
    @Test
    void testModifyExpense_ValidRequest_ModifiesExpenseCorrectly() {
        YearMonth testMonthYear = YearMonth.of(2000, 1);
        Expense expense = new Expense("Rent", 1000.0, ExpenseCategory.HOUSING, TransactionType.RECURRING, testMonthYear);
        
        ExpenseRequest updateRequest = new ExpenseRequest();
        updateRequest.setName("Groceries");
        updateRequest.setAmount(200.0);
        updateRequest.setCategory(ExpenseCategory.GROCERIES);
        updateRequest.setType(TransactionType.ONE_TIME);
        updateRequest.setMonthYear("2000-01");

        FinancialModelUtils.modifyExpense(expense, updateRequest);
        
        assertEquals("GROCERIES", expense.getName(), "Name should be capitalized");
        assertEquals(200.0, expense.getAmount(), "Amount should match");
        assertEquals(ExpenseCategory.GROCERIES, expense.getCategory(), "Expense category should match");
        assertEquals(TransactionType.ONE_TIME, expense.getType(), "Transaction type should match");
        assertEquals(YearMonth.of(2000, 1), expense.getMonthYear(), "MonthYear should be correctly set");
    }
}
