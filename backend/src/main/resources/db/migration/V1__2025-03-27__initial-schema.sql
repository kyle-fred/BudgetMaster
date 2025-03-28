-- Create Budget Table
CREATE TABLE public.budget (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    income DOUBLE PRECISION NOT NULL,
    expenses DOUBLE PRECISION NOT NULL,
    savings DOUBLE PRECISION NOT NULL,
    month_year VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    last_updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_budget_month_year ON public.budget (month_year);

-- Create Income Table
CREATE TABLE public.income (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    source VARCHAR(255) NOT NULL,
    amount DOUBLE PRECISION NOT NULL,
    month_year VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    last_updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Create Expense Table
CREATE TABLE public.expense (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(255) NOT NULL,
    amount DOUBLE PRECISION NOT NULL,
    month_year VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    last_updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Create function to update last_updated_at timestamp
CREATE OR REPLACE FUNCTION update_last_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.last_updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create triggers for each table to update last_updated_at before update
CREATE TRIGGER set_last_updated_at_on_budget
BEFORE UPDATE ON budget
FOR EACH ROW
EXECUTE FUNCTION update_last_updated_at_column();

CREATE TRIGGER set_last_updated_at_on_income
BEFORE UPDATE ON income
FOR EACH ROW
EXECUTE FUNCTION update_last_updated_at_column();

CREATE TRIGGER set_last_updated_at_on_expense
BEFORE UPDATE ON expense
FOR EACH ROW
EXECUTE FUNCTION update_last_updated_at_column();
