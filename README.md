# **BudgetMaster: A Personal Finance Management Application**

## **📌 Overview**
Welcome to **BudgetMaster** – a personal finance application designed to help track monthly expenses, savings, and income.  
This app enables users to **create budgets**, **categorize expenses/income**, and **gain financial insights**.  

### **🚀 Features**
✅ **Track Monthly Budgets** – Define a monthly budget by calculating total income and expenses.  
✅ **Manage Incomes & Expenses** – Categorize incomes (e.g., Salary, Investments) and expenses (e.g., Rent, Bills).  
✅ **Recurring & One-Time Transactions** – Distinguish between fixed and variable financial transactions.  
✅ **RESTful API** – Well-structured API endpoints to interact with the system programmatically.  

---

## **🛠️ Technologies Used**
| Stack | Technology |
|--------|----------------|
| **Backend** | Java, Spring Boot |
| **Database** | PostgreSQL, JPA (Hibernate) |
| **Testing** | JUnit, Mockito, SpringBootTest |
| **API Docs (Planned)** | SpringDoc OpenAPI (Swagger) |
| **Frontend (Planned)** | React (Vite) |

---

## **📡 API Endpoints**
BudgetMaster currently has **three main API endpoints**:  

| Endpoint | Description |
|----------|-------------|
| `/api/budget` | Manage overall budgeting (income, expenses, savings). |
| `/api/income` | Manage income sources (salary, investments, side hustles). |
| `/api/expense` | Manage expenses (bills, subscriptions, purchases). |

Each API has full **CRUD functionality**.  
Here’s how you can test them with **cURL commands**.

---

## **📊 Budget API (`/api/budget`)**
### **📌 Model**
```json
{
    "id": 1,
    "income": 5000,
    "expenses": 2000,
    "savings": 3000
}
```

### **📌 Example Usage**
### **✅ Create Budget (POST)**
```bash
curl -X POST http://localhost:8080/api/budget \
     -H "Content-Type: application/json" \
     -d '{"income": 5000, "expenses": 2000}'
```
### **✅ Get Budget by ID (GET)**
```bash
curl -X GET http://localhost:8080/api/budget/1
```
### **✅ Update Budget (PUT)**
```bash
curl -X PUT http://localhost:8080/api/budget/1 \
     -H "Content-Type: application/json" \
     -d '{"income": 5500, "expenses": 2100}'
```
### **✅ Delete Budget (DELETE)**
```bash
curl -X DELETE http://localhost:8080/api/budget/1
```

---

## **💰 Income API (`/api/income`)**
### **📌 Model**
```json
{
    "id": 1,
    "name": "Salary",
    "source": "Company XYZ",
    "amount": 5000,
    "transactionType": "RECURRING"
}
```

### **📌 Example Usage**
### **✅ Create Income (POST)**
```bash
curl -X POST http://localhost:8080/api/income \
     -H "Content-Type: application/json" \
     -d '{"name": "Salary", "source": "Company XYZ", "amount": 5000, "transactionType": "RECURRING"}'
```
### **✅ Get Income by ID (GET)**
```bash
curl -X GET http://localhost:8080/api/income/1
```
### **✅ Update Income (PUT)**
```bash
curl -X PUT http://localhost:8080/api/income/1 \
     -H "Content-Type: application/json" \
     -d '{"name": "Freelance Work", "source": "Client XYZ", "amount": 2000, "transactionType": "ONE_TIME"}'
```
### **✅ Delete Income (DELETE)**
```bash
curl -X DELETE http://localhost:8080/api/income/1
```

---

## **💳 Expense API (`/api/expense`)**
### **📌 Model**
```json
{
    "id": 1,
    "name": "Rent",
    "amount": 1000,
    "expenseCategory": "HOUSING",
    "transactionType": "RECURRING"
}
```

### **📌 Example Usage**
### **✅ Create Expense (POST)**
```bash
curl -X POST http://localhost:8080/api/expense \
     -H "Content-Type: application/json" \
     -d '{"name": "Rent", "amount": 1000, "expenseCategory": "HOUSING", "transactionType": "RECURRING"}'
```
### **✅ Get Expense by ID (GET)**
```bash
curl -X GET http://localhost:8080/api/expense/1
```
### **✅ Update Expense (PUT)**
```bash
curl -X PUT http://localhost:8080/api/expense/1 \
     -H "Content-Type: application/json" \
     -d '{"name": "Gas & Electricity", "amount": 115, "expenseCategory": "UTILITIES", "transactionType": "RECURRING"}'
```
### **✅ Delete Expense (DELETE)**
```bash
curl -X DELETE http://localhost:8080/api/expense/1
```

---

### **🛠️ Running the Application**
```bash
mvn spring-boot:run
```

### **🧪 Running Tests**
```bash
mvn test
```