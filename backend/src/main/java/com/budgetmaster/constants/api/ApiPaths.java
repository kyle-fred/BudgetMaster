package com.budgetmaster.constants.api;

public class ApiPaths {

    public static final String BASE = "/api";

    public static class Budgets {
        public static final String ROOT = BASE + "/budgets";
        public static final String BY_ID = ROOT + "/{id}";  
    }

    public static class Incomes {
        public static final String ROOT = BASE + "/incomes";
        public static final String BY_ID = ROOT + "/{id}";
    }

    public static class Expenses {
        public static final String ROOT = BASE + "/expenses";
        public static final String BY_ID = ROOT + "/{id}";
    }
}
