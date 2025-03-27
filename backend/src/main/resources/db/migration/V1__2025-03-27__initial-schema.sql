-- Create Budget Table
CREATE TABLE public.budget (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    income DOUBLE PRECISION NOT NULL,
    expenses DOUBLE PRECISION NOT NULL,
    savings DOUBLE PRECISION NOT NULL,
    month_year VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL,
    last_updated_at TIMESTAMP NOT NULL
);
CREATE INDEX idx_budget_month_year ON public.budget (month_year);

-- Create Income Table
CREATE TABLE public.income (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    amount DOUBLE PRECISION NOT NULL,
    created_at TIMESTAMP NOT NULL,
    last_updated_at TIMESTAMP NOT NULL,
    month_year VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    source VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL
);

-- Create Expense Table
CREATE TABLE public.expense (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    amount DOUBLE PRECISION NOT NULL,
    created_at TIMESTAMP NOT NULL,
    last_updated_at TIMESTAMP NOT NULL,
    type VARCHAR(255) NOT NULL,
    month_year VARCHAR(255) NOT NULL,
    category VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL
);