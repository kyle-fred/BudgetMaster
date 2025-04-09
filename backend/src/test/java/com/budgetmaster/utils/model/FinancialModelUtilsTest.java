package com.budgetmaster.utils.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.YearMonth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.budgetmaster.dto.BudgetRequest;
import com.budgetmaster.dto.IncomeRequest;
import com.budgetmaster.dto.ExpenseRequest;
import com.budgetmaster.enums.ExpenseCategory;
import com.budgetmaster.enums.TransactionType;
import com.budgetmaster.model.Budget;
import com.budgetmaster.model.Income;
import com.budgetmaster.model.Expense;

public class FinancialModelUtilsTest {

    private BudgetRequest budgetRequest;
    private IncomeRequest incomeRequest;
    private ExpenseRequest expenseRequest;

    @BeforeEach
    void setUp() {
        budgetRequest = new BudgetRequest();
        budgetRequest.setTotalIncome(5000D);
        budgetRequest.setTotalExpense(2000D);
        budgetRequest.setMonthYear("2000-01");

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

    // ----- Budget Tests -----
    @Test
    void testBuildBudgetRequest_SetsValidMonthYear() {
        BudgetRequest result = FinancialModelUtils.buildBudgetRequest(budgetRequest);
        
        assertEquals("2000-01", result.getMonthYear(), "Should correctly set the monthYear");
    }

    @Test
    void testBuildBudget_CreatesBudgetWithValidValues() {
        Budget budget = FinancialModelUtils.buildBudget(budgetRequest);
        
        assertNotNull(budget, "Budget object should not be null");
        assertEquals(5000, budget.getTotalIncome(), "Income should match");
        assertEquals(2000, budget.getTotalExpense(), "Expenses should match");
        assertEquals(YearMonth.of(2000, 1), budget.getMonthYear(), "MonthYear should be correctly set");
    }
    
    @Test
    void testModifyBudget_ValidRequest_ModifiesBudgetCorrectly() {
        Budget budget = new Budget(3000.0, 1500.0, YearMonth.of(2000, 1));
        
        BudgetRequest request = new BudgetRequest();
        request.setTotalIncome(4000.0);
        request.setTotalExpense(2000.0);
        request.setMonthYear("2000-02");

        FinancialModelUtils.modifyBudget(budget, request);

        assertEquals(4000.0, budget.getTotalIncome(), "Income should be updated");
        assertEquals(2000.0, budget.getTotalExpense(), "Expenses should be updated");
        assertEquals(YearMonth.of(2000, 2), budget.getMonthYear(), "MonthYear should be updated");
    }

    @Test
    void testModifyBudget_NullMonthYear_DoesNotChangeMonthYear() {
        YearMonth originalMonthYear = YearMonth.of(2000, 1);
        
        Budget budget = new Budget(3000.0, 1500.0, originalMonthYear);
        BudgetRequest request = new BudgetRequest();
        request.setTotalIncome(4000.0);
        request.setTotalExpense(2000.0);
        request.setMonthYear(null);

        FinancialModelUtils.modifyBudget(budget, request);

        assertEquals(originalMonthYear, budget.getMonthYear(), "MonthYear should remain unchanged");
    }

    @Test
    void testModifyBudget_EmptyMonthYear_DoesNotChangeMonthYear() {
        YearMonth originalMonthYear = YearMonth.of(2023, 5);
        
        Budget budget = new Budget(3000.0, 1500.0, originalMonthYear);
        BudgetRequest request = new BudgetRequest();
        request.setTotalIncome(4000.0);
        request.setTotalExpense(2000.0);
        request.setMonthYear("");

        FinancialModelUtils.modifyBudget(budget, request);

        assertEquals(originalMonthYear, budget.getMonthYear(), "MonthYear should remain unchanged when empty");
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
