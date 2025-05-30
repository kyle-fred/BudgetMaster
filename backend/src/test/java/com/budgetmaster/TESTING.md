# **BudgetMaster: Testing Standards**

## **1.📁 Readability**
<br>

#### **1.1 Naming Convention**
✅ Method Naming
- Format: `methodName_condition_expectedResult`
<br><br>

#### **1.2 Display Name**
✅ JUnit `@DisplayName`:
- Use in controller tests and integration tests to clarify HTTP interactions.
```java
@DisplayName("GET /budget returns 200 for valid month")
@Test
void getBudget_validMonth_returnsOk() { ... }
```
<br>

#### **1.3 Test Structure**
✅ Use `@Nested` classes to group tests logically
```java
@Nested
@DisplayName("GET /budget returns 200 for valid month")
class GetBudget {
    @Test
    void returnsOk_forValidMonth() { ... }
}
```
❌ **Avoid mixing unrelated methods in one test class without separation.**  
❌ **Avoid duplicating function name in the test name, and test class.**

---

## **2.✅ Assertions**

#### **2.1 Assert Syntax**
Use:
```java
assertThat(actual).isEqualTo(expected);
```
Not:
```java
assertThat(expected).isEqualTo(actual); // Confusing failure messages
```
<br>

#### **2.2 Assertion Helpers**
✅ Create assertion helper classes for each domain in testsupport/assertions/  
❌ Avoid repeating long assertion chains.
<br>

#### **2.3 Controller `.andExpect()` chains**
✅ Move long repetitive mockMvc.perform(...) chains into ControllerTestHelper.java files in testsupport/controller  
❌ Avoid repeating long .andExpect(...) chains.
<br>

#### **2.4 `isEqualTo()` vs `isEqualByComparingTo()`**
- `isEqualByComparingTo()` is used for BigDecimal value-based comparisons, ignoring scale.
- `isEqualTo()` compares exact object equality.

✅ Rule of Thumb:
- For BigDecimal: Always use `.isEqualByComparingTo(...)`
- For strings, enums, primitives: Use `.isEqualTo(...)`

---


## **3.🔢 Constants Management**
<br>

#### **3.1 Structure**
✅ Split constants by concern and domain
```java
errorMessage/
├── BudgetMessages.java
├── IncomeMessages.java
└── GlobalErrorMessages.java
```
❌ Avoid deep nested classes like ErrorMessage.Budget.SOMETHING.
<br><br>

#### **3.2 Formatting**
✅ Align = signs for readability:
```java
public static final String ERROR_ONE   = "...";
public static final String ERROR_TWO   = "...";
public static final String LONGER_ONE  = "...";
```
✅ Can use the 'Align' extension in VSCode to do this.
<br>
---
<br>

## **4.🏗 Builders vs Factories**

#### ✅ Prefer Builder pattern over Factories
- Provides fine-grained control
- Cleaner for nested object creation
- Encourages chaining

#### 📁 Keep all builder classes in testsupport/builder/
```java
Income income = IncomeBuilder.anIncome().withAmount(...).withDate(...).build();
```

---

## **5.🚫 Warnings**

✅ Use @SuppressWarnings only with justification in the form of a comment.  
✅ Suppress the minimal scope necessary (null, unchecked, etc).
- Prevents real warnings being lost

```java
@SuppressWarnings("null") // Expected null for edge case test
@Test
void handlesNullGracefully() { ... }
```
❌ Never suppress "all" warnings.

--- 

## **6.🏷 JUnit Tags**

Use @Tag to separate test types
Helps when running test groups:

```java
@Tag("integration")
@Tag("unit")
```
Then run with:
```bash
mvn test -Dgroups=unit
```
✅ Good for CI pipelines where you may want to separate long-running integration tests from fast unit tests.

--- 