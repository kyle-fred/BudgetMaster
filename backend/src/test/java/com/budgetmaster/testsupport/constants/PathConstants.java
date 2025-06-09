package com.budgetmaster.testsupport.constants;

public final class PathConstants {
  private PathConstants() {}

  public static class Endpoints {
    private Endpoints() {}

    public static final String BASE = "/api";
    public static final String TEST = BASE + "/test";

    public static final String BUDGET = BASE + "/budgets";
    public static final String BUDGET_WITH_ID = BUDGET + "/{id}";

    public static final String EXPENSE = BASE + "/expenses";
    public static final String EXPENSE_WITH_ID = EXPENSE + "/{id}";

    public static final String INCOME = BASE + "/incomes";
    public static final String INCOME_WITH_ID = INCOME + "/{id}";
  }

  public static class Error {
    private Error() {}

    public static final String URI_BASE = "uri=";

    public static class Budget {
      private Budget() {}

      public static final String URI = URI_BASE + Endpoints.BUDGET;
      public static final String URI_WITH_ID = URI + "/%s";
    }

    public static class Expense {
      private Expense() {}

      public static final String URI = URI_BASE + Endpoints.EXPENSE;
      public static final String URI_WITH_ID = URI + "/%s";
    }

    public static class Income {
      private Income() {}

      public static final String URI = URI_BASE + Endpoints.INCOME;
      public static final String URI_WITH_ID = URI + "/%s";
    }
  }

  public static class JsonProperties {
    private JsonProperties() {}

    public static final String BASE = "$";
    public static final String LENGTH = BASE + ".length()";
    public static final String SINGLE_OBJECT = BASE + "[%s]";

    // -- Budget --
    public static final String TOTAL_INCOME = ".total-income";
    public static final String TOTAL_EXPENSE = ".total-expense";
    public static final String SAVINGS = ".savings";

    // -- Income & Expense --
    public static final String NAME = ".name";
    public static final String SOURCE = ".source";
    public static final String MONEY = ".money";
    public static final String CATEGORY = ".category";
    public static final String TYPE = ".type";
    public static final String MONTH_YEAR = ".month";

    public static class Month {
      private Month() {}

      public static final String YEAR_VALUE = MONTH_YEAR + "[0]";
      public static final String MONTH_VALUE = MONTH_YEAR + "[1]";
    }

    public static class Money {
      private Money() {}

      public static final String AMOUNT = ".amount";
      public static final String CURRENCY = ".currency";
    }

    public static class Error {
      private Error() {}

      public static final String TIMESTAMP = "$.timestamp";
      public static final String STATUS = "$.status";
      public static final String CODE = "$.error-code";
      public static final String MESSAGE = "$.message";
      public static final String PATH = "$.path";
      public static final String VALIDATION_ERRORS = "$.errors";
    }
  }

  public static class RequestParams {
    private RequestParams() {}

    public static final String MONTH = "month";
  }
}
