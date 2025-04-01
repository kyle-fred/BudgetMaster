-- Create Budget Table with savings as a generated column
CREATE TABLE public.budget (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    total_income DOUBLE PRECISION NOT NULL,
    total_expense DOUBLE PRECISION NOT NULL,
    savings DOUBLE PRECISION GENERATED ALWAYS AS (ROUND((total_income - total_expense)::NUMERIC, 2)::DOUBLE PRECISION) STORED,
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

-- Create a function to update the budget table whenever income is modified
CREATE OR REPLACE FUNCTION update_budget_total_income()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' OR (TG_OP = 'UPDATE' AND NEW.month_year <> OLD.month_year) THEN
        IF NOT EXISTS (SELECT 1 FROM budget WHERE month_year = NEW.month_year) THEN
            INSERT INTO budget (month_year, total_income, total_expense, created_at, last_updated_at)
            VALUES (NEW.month_year, 0, 0, NOW(), NOW());
        END IF;
    END IF;

    IF TG_OP = 'INSERT' OR TG_OP = 'UPDATE' THEN
        IF TG_OP = 'UPDATE' AND OLD.month_year <> NEW.month_year THEN
            UPDATE budget
            SET total_income = (SELECT COALESCE(SUM(amount), 0) FROM income WHERE month_year = OLD.month_year),
                last_updated_at = NOW()
            WHERE month_year = OLD.month_year;
        END IF;

        UPDATE budget
        SET total_income = (SELECT COALESCE(SUM(amount), 0) FROM income WHERE month_year = NEW.month_year),
            last_updated_at = NOW()
        WHERE month_year = NEW.month_year;
    END IF;

    IF TG_OP = 'DELETE' THEN
        UPDATE budget
        SET total_income = (SELECT COALESCE(SUM(amount), 0) FROM income WHERE month_year = OLD.month_year),
            last_updated_at = NOW()
        WHERE month_year = OLD.month_year;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create triggers for INSERT, UPDATE, DELETE for income table
CREATE TRIGGER sync_budget_total_income
AFTER INSERT OR UPDATE OR DELETE ON income
FOR EACH ROW
EXECUTE FUNCTION update_budget_total_income();

-- Create a function to update the budget table whenever expense is modified
CREATE OR REPLACE FUNCTION update_budget_total_expense()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' OR (TG_OP = 'UPDATE' AND NEW.month_year <> OLD.month_year) THEN
        IF NOT EXISTS (SELECT 1 FROM budget WHERE month_year = NEW.month_year) THEN
            INSERT INTO budget (month_year, total_income, total_expense, created_at, last_updated_at)
            VALUES (NEW.month_year, 0, 0, NOW(), NOW());
        END IF;
    END IF;

    IF TG_OP = 'INSERT' OR TG_OP = 'UPDATE' THEN
        IF TG_OP = 'UPDATE' AND OLD.month_year <> NEW.month_year THEN
            UPDATE budget
            SET total_expense = (SELECT COALESCE(SUM(amount), 0) FROM expense WHERE month_year = OLD.month_year),
                last_updated_at = NOW()
            WHERE month_year = OLD.month_year;
        END IF;

        UPDATE budget
        SET total_expense = (SELECT COALESCE(SUM(amount), 0) FROM expense WHERE month_year = NEW.month_year),
            last_updated_at = NOW()
        WHERE month_year = NEW.month_year;
    END IF;

    IF TG_OP = 'DELETE' THEN
        UPDATE budget
        SET total_expense = (SELECT COALESCE(SUM(amount), 0) FROM expense WHERE month_year = OLD.month_year),
            last_updated_at = NOW()
        WHERE month_year = OLD.month_year;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create triggers for INSERT, UPDATE, DELETE for expense table
CREATE TRIGGER sync_budget_total_expense
AFTER INSERT OR UPDATE OR DELETE ON expense
FOR EACH ROW
EXECUTE FUNCTION update_budget_total_expense();
