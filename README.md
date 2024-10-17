# Rule Engine with AST

This project implements a **Rule Engine** using an **Abstract Syntax Tree (AST)** to parse, store, and evaluate user-defined rules. The application is built with **Java**, **Spring Boot**, and **PostgreSQL**, and it provides RESTful APIs to create and evaluate rules. The application allows dynamic rule creation and evaluation based on user-provided data.

## Features

- **Rule Creation**: Dynamically create rules using a string format like `age > 30 AND department = 'Sales'`.
- **Rule Evaluation**: Evaluate rules using the AST representation against user data provided in JSON format.
- **Parentheses Handling**: Supports complex rules with parentheses, such as `((age > 30 AND department = 'Sales') OR (age < 25))`.
- **Error Handling**: Automatically attempts to balance parentheses in case of mismatches and provides detailed error messages for invalid rules.

## Technologies Used

- **Java 17**
- **Spring Boot**
- **Spring Data JPA**
- **PostgreSQL**
- **Maven**
- **Postman**
- **IntelliJ**

## Prerequisites

### Local Setup

1. **Java 17+**: Ensure that you have JDK 17 or later installed.
2. **Maven 3.8+**: Make sure Maven is installed to build the project.
3. **PostgreSQL 12+**: Either install PostgreSQL locally or use Docker to run PostgreSQL.
4. **IntelliJ Idea**: To run the project. 

To run PostgreSQL in a Docker container, execute the following command:

```bash
docker run --name postgres -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=your-password -e POSTGRES_DB=rule_engine -p 5432:5432 -d postgres
```

## Project SetUp

1. Clone the Repository:
   ```
    git clone https://github.com/your-username/rule-engine-ast.git
    cd rule-engine-ast
   ```
   
2. Configure PostgreSQL:
   Create a Database called rule_engine 
   ```
    psql -U postgres
    CREATE DATABASE rule_engine;
   ```

3. Configure Application.Properties:
   ```
    spring.datasource.url=${DB_URL}
    spring.datasource.username=${DB_USERNAME}
    spring.datasource.password=${DB_PASSWORD}
    spring.datasource.driver-class-name=org.postgresql.Driver

    spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
    spring.jpa.properties.hibernate.format_sql=true
    spring.jpa.hibernate.ddl-auto=create-drop
    spring.jpa.show-sql=true
   ```

4. Build and run the application on localhost:
   ```
    mvn clean install
    mvn spring-boot:run
   ```

   The application will start at http://localhost:8080/

## Testing via Web Interface:

### HTML frontend (index.html)
   
1. A simple frontend is available in src/main/resources/static/index.html that allows you to:

   Create Rules by entering a rule name and rule string.
   Evaluate Rules by entering rule data (age, department, salary, etc.).
   
   Steps:
     Navigate to http://localhost:8080/index.html.
     Use the provided form to create a new rule.
     Enter user data in the evaluation form and evaluate the rule.

3. Example Usage:

   Create Rule:

     Rule Name: Rule1
     Rule String:
     ```
       ((age > 30 AND department = 'Sales') OR (age < 25 AND department = 'Marketing')) AND (salary > 50000)
    ```
     Evaluate Rule:
    ```
       Age: 35
       Department: Sales
       Salary: 60000
       Expected Result: true (rule is satisfied).
    ```
   
## Design Choices:

1. AST Representation: The rule engine uses an Abstract Syntax Tree (AST) to represent rules for easy evaluation.
2. Error Handling: The application includes error handling for invalid rules, ensuring that unbalanced parentheses and unsupported operators are properly reported.
3. Separation of Concerns: The project follows a clean architecture, separating the controller, service, and repository layers.

## Project Structure:

```
  src
├── main
│   ├── java
│   │   └── org/pragadeesh/astnode
│   │       ├── controller  // REST API controllers
│   │       ├── entity      // JPA entities (Rule, ASTNode)
│   │       ├── repository  // Spring Data repositories
│   │       └── service     // Rule and AST processing services
│   └── resources
│       ├── static          // Frontend HTML (index.html)
│       └── application.properties  // PostgreSQL config
└── test
    └── java
        └── org/pragadeesh/astnode  // Unit and integration tests

```
## Error Handling:

1. Unbalanced Parentheses: The application automatically attempts to balance unbalanced parentheses in rule strings.
2. Invalid Conditions: Provides detailed error messages for invalid rule formats or unsupported operators.

## Contributions:

Contributions are welcome! Please open an issue or submit a pull request for any changes or enhancements.
