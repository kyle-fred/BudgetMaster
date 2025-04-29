package com.budgetmaster.constants.api;

public class ApiPaths {

    public static final String BASE = "/api";
    public static final String SEARCH_BY_ID = "/{id}";

    public static class Budgets {
        public static final String ROOT = BASE + "/budgets";
    }

    public static class Incomes {
        public static final String ROOT = BASE + "/incomes";
    }

    public static class Expenses {
        public static final String ROOT = BASE + "/expenses";
    }
}
