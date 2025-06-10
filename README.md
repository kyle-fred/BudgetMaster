# **BudgetMaster: A Personal Finance Management Application**

## **📌 Overview**
**BudgetMaster** is a robust personal finance management application built with Spring Boot that helps users track their monthly finances. The application automatically calculates and maintains budget summaries based on income and expense entries, providing a clear view of your financial health.

### **🚀 Features**
✅ **Monthly Budget Tracking** - Automatic calculation of total income, expenses, and savings for each month  
✅ **Transaction Management** - Track various income / expenses with support for recurring and one-time transactions  
✅ **RESTful API** - Well-structured, documented API endpoints

---

## **🛠️ Technologies Used**
| Component | Technology |
|-----------|------------|
| **Backend** | Java, Spring Boot |
| **Database** | PostgreSQL & JPA/Hibernate |
| **Testing** | JUnit, Mockito & Test Containers |
| **API Documentation** | SpringDoc OpenAPI (Swagger) |
| **Frontend** | React |

---

## **📡 API Endpoints**
BudgetMaster has **three endpoints**:  

| Endpoint | Description |
|----------|-------------|
| `/api/budgets` | Retrieve overall budgeting health (income, expenses, savings). |
| `/api/incomes` | Manage income sources (salary, investments, side hustles). |
| `/api/expenses` | Manage expenses (bills, subscriptions, purchases). |

---

## **📊 Budget API (`/api/budgets`)**  
  
### <ins>Get Budget by Month</ins>

```http
GET /api/budgets?month=YYYY-MM
```
Returns the budget summary for the specified month, including:
- Total income
- Total expenses
- Savings

### <ins>Delete Budget</ins>
```http
DELETE /api/budgets/{id}
```

---

## **💸 Income API (`/api/incomes`)**  
Manage your income sources with full CRUD operations.

### <ins>Create Income</ins>
```http
POST /api/incomes
Content-Type: application/json

{
    "name": "SALARY",
    "source": "COMPANY XYZ",
    "money": {
        "amount": 5000.00,
        "currency": "USD"
    },
    "type": "RECURRING",
    "month": "2025-06"
}
```

### <ins>Get Incomes for Month</ins>
```http
GET /api/incomes?month=YYYY-MM
```

### <ins>Update Income</ins>
```http
PUT /api/incomes/{id}
Content-Type: application/json

{
    "name": "FREELANCE WORK",
    "source": "CLIENT XYZ",
    "money": {
        "amount": 2000.00,
        "currency": "USD"
    },
    "type": "ONE_TIME",
    "month": "2025-05"
}
```

### <ins>Delete Income</ins>
```http
DELETE /api/incomes/{id}
```

---

## **💰 Expense API (`/api/expenses`)**  
Manage your expenses with full CRUD operations.

### <ins>Create Expense</ins>
```http
POST /api/expenses
Content-Type: application/json

{
    "name": "RENT",
    "money": {
        "amount": 1500.00,
        "currency": "USD"
    },
    "category": "HOUSING",
    "type": "RECURRING",
    "month": "2024-03"
}
```

### <ins>Get Expenses for Month</ins>
```http
GET /api/expenses?month=YYYY-MM
```

### <ins>Update Expense</ins>
```http
PUT /api/expenses/{id}
Content-Type: application/json

{
    "name": "UTILITIES",
    "money": {
        "amount": 200.00,
        "currency": "USD"
    },
    "category": "UTILITIES",
    "type": "RECURRING",
    "month": "2024-03"
}
```

### <ins>Delete Expense</ins>
```http
DELETE /api/expenses/{id}
```

---

## **🏗️ Architecture**
The application follows a clean, layered architecture:
- **Controllers** - Handle HTTP requests and responses
- **Services** - Implement business logic and transaction management
- **Repositories** - Manage data persistence
- **Models** - Define the domain entities
- **DTOs** - Handle data transfer between layers
- **Synchronizers** - Maintain consistency between budgets and transactions

---

## **🚀 Getting Started**

### Prerequisites
- Java 17 or higher
- Maven
- PostgreSQL

### Running the Application
1. Clone the repository
2. Configure your database connection in `application.properties`
3. Run the application:
```bash
mvn spring-boot:run
```

### **🧪 Running Tests**  

#### Prerequisites
- Docker Engine (Integration Tests use Test Containers)

```bash
mvn test
```

## 🔜 Future Enhancements
- Frontend implementation with React
- API documentation with Swagger/OpenAPI
- User authentication and authorization
- Calendar Integration
- Data visualization and reporting
- Export functionality