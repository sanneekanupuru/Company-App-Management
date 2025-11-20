# Company App Management API

A Spring Boot + JPA/Hibernate application demonstrating One-to-Many, Many-to-Many, and One-to-One relationships with properly structured JSON responses.

📌 Entity Relationships

Company → Projects : One-to-Many

Project → Employees : Many-to-Many

Employee → Address : One-to-One (Cascade ALL, orphan removal)

⭐ API ENDPOINTS WITH REQUEST & RESPONSE EXAMPLES

## SCENARIO 1 — Employee & Address
1.1 Get Employee (LAZY fetch)

Request (Postman)

Method: GET

URL: http://localhost:8080/api/employees/1/lazy

Headers:

Accept: application/json

Response (200 OK)
```
{
"id": 1,
"firstName": "Sannee",
"lastName": "Kanupuru",
"email": "sannee@gmail.com",
"address": {
"id": 1,
"street": "Kothaguda",
"city": "Hyderabad",
"state": "TS",
"country": "India",
"zip": "500084"
},
"projectIds": [1]
}
```

Notes: Service initializes lazy associations inside a @Transactional method so address and projectIds are present.

1.2 Get Employee (EAGER fetch-join)

Request (Postman)

Method: GET

URL: http://localhost:8080/api/employees/1/eager

Headers:

Accept: application/json

Response (200 OK)
```
{
"id": 1,
"firstName": "Sannee",
"lastName": "Kanupuru",
"email": "sannee@gmail.com",
"address": {
"id": 1,
"street": "Kothaguda",
"city": "Hyderabad",
"state": "TS",
"country": "India",
"zip": "500084"
},
"projectIds": [1]
}
```

Notes: Uses JPQL fetch join to load employee + address + projects in one SQL.

1.3 Delete Employee (address auto-deleted)

Request (Postman)

Method: DELETE

URL: http://localhost:8080/api/employees/2

Headers:

Accept: application/json

Response (204 No Content)

Body: empty

Behavior explanation

Service removes employee associations from projects (owning side) and deletes the employee. Because Employee.address is cascade = ALL and orphanRemoval = true, the address row is deleted automatically.

## SCENARIO 2 — Projects ↔ Employees (Many-to-many)
2.1 Fetch all employees working on a given project

Request (Postman)

Method: GET

URL: http://localhost:8080/api/projects/1/employees

Headers:

Accept: application/json

Response (200 OK)
```
[
{
"id": 1,
"firstName": "Sannee",
"lastName": "Kanupuru",
"email": "sannee@gmail.com",
"address": {
"id": 1,
"street": "Kothaguda",
"city": "Hyderabad",
"state": "TS",
"country": "India",
"zip": "500084"
},
"projectIds": [1]
},
{
"id": 2,
"firstName": "Bharath",
"lastName": "Kumar",
"email": "bharath@gmail.com",
"address": {
"id": 2,
"street": "Madhapur",
"city": "Hyderabad",
"state": "TS",
"country": "India",
"zip": "500081"
},
"projectIds": [1]
}
]
```

Notes: Controller maps Employee → EmployeeDto before returning to avoid leaking JPA entities.

2.2 Fetch all projects assigned to a specific employee

Request (Postman)

Method: GET

URL: http://localhost:8080/api/employees/1/projects

Headers:

Accept: application/json

Response (200 OK)
```
[
{
"id": 1,
"name": "Alpha"
}
]
```

Notes: Returns ProjectDto (id + name). Employee removal does not delete projects; projects remain.

## SCENARIO 3 — Company → Projects → Employees
3.1 Fetch company by name (full tree: company → projects → employees)

Request (Postman)

Method: GET

URL: http://localhost:8080/api/companies/name/TechCorp

Headers:

Accept: application/json

Response (200 OK)
```
{
"id": 1,
"name": "TechCorp",
"projects": [
{
"id": 1,
"name": "Alpha",
"employees": [
{
"id": 1,
"firstName": "Sannee",
"lastName": "Kanupuru",
"email": "sannee@gmail.com",
"address": {
"id": 1,
"street": "Kothaguda",
"city": "Hyderabad",
"state": "TS",
"country": "India",
"zip": "500084"
},
"projectIds": [1]
},
{
"id": 2,
"firstName": "Bharath",
"lastName": "Kumar",
"email": "bharath@gmail.com",
"address": {
"id": 2,
"street": "Madhapur",
"city": "Hyderabad",
"state": "TS",
"country": "India",
"zip": "500081"
},
"projectIds": [1]
}
]
},
{
"id": 2,
"name": "Beta",
"employees": [
{
"id": 3,
"firstName": "Alice",
"lastName": "Reddy",
"email": "alice@gmail.com",
"address": null,
"projectIds": [2]
}
]
}
]
}
```

Notes: CompanyRepository.findByNameWithProjectsAndEmployees(name) uses HQL left join fetch to fetch all related data in one query (parent table queried; children come via joins).

3.2 Delete company with cascade (delete company → delete projects, employees, addresses)

Request (Postman)

Method: DELETE

URL: http://localhost:8080/api/companies/1/cascade

Headers:

Accept: application/json

Response (204 No Content)

Body: empty

Behavior

Company has cascade = ALL, orphanRemoval = true on projects. Implementation in service deletes company; entity cascade & orphan removal delete projects. Depending on mapping you set, employees and addresses are also removed (ensure you intentionally set cascades if you require employee deletion).

3.3 Delete company but keep children (projects & employees)

Request (Postman)

Method: DELETE

URL: http://localhost:8080/api/companies/1/keep

Headers:

Accept: application/json

Response (204 No Content)

Body: empty

Behavior

Service implementation unlinks projects from the company, clears company.projects and deletes the company while leaving projects and employees intact.

## SCENARIO 4 — Update Employee (and Address)
4.1 Update employee (PUT) — updates both employee and address

Request (Postman)

Method: PUT

URL: http://localhost:8080/api/employees/1

Headers:

Content-Type: application/json

Accept: application/json

Body (raw JSON):
```
{
"firstName": "Sannee",
"lastName": "Kanupuru",
"email": "sannee@gmail.com",
"address": {
"street": "New Street",
"city": "Hyderabad",
"state": "TS",
"country": "India",
"zip": "500090"
}
}
```

Response (200 OK)
```
{
"id": 1,
"firstName": "Sannee",
"lastName": "Kanupuru",
"email": "sannee@gmail.com",
"address": {
"id": 1,
"street": "New Street",
"city": "Hyderabad",
"state": "TS",
"country": "India",
"zip": "500090"
},
"projectIds": [1]
}
```

Behavior

EmployeeMapper.updateEntityFromDto updates the Employee entity and nested Address entity. Save is in @Transactional so both are persisted.

## SCENARIO 5 — Preload DB (data.sql)

My src/main/resources/data.sql (already present) runs automatically (because spring.sql.init.mode=always and spring.jpa.defer-datasource-initialization=true in application.properties) after schema creation.

Contents (example):

```
-- 1) Insert one company
INSERT INTO companies (id, name) VALUES
 (1, 'TechCorp');

-- 2) Insert two projects for the company
INSERT INTO projects (id, name, company_id) VALUES
 (1, 'Alpha', 1),
 (2, 'Beta', 1);

-- 3) Insert addresses (for one-to-one mapping)
INSERT INTO addresses (id, street, city, state, country, zip) VALUES
 (1, 'Kothaguda', 'Hyderabad', 'TS', 'India', '500084'),
 (2, 'Madhapur', 'Hyderabad', 'TS', 'India', '500081');

-- 4) Insert employees (each linked to an address)
INSERT INTO employees (id, first_name, last_name, email, address_id) VALUES
 (1, 'Sannee',  'Kanupuru', 'sannee@gmail.com', 1),
 (2, 'Bharath', 'Kumar', 'bharath@gmail.com', 2),
 (3, 'Alice',   'Reddy', 'alice@gmail.com', NULL); -- employee without address

-- 5) Insert project-employee relation (many-to-many)
INSERT INTO project_employees (project_id, employee_id) VALUES
 (1, 1),  -- Sannee → Alpha
 (1, 2),  -- Bharath → Alpha
 (2, 3);  -- Alice → Beta

```

To verify the data (Postman):

Request: GET http://localhost:8080/api/companies/name/TechCorp

Response: company tree as shown in Scenario 3.1

## SCENARIO 6 — Pagination & Sorting (Project employees)
6.1 Get paged employees for a project

Request (Postman)

Method: GET

URL example:

http://localhost:8080/api/projects/1/employees/paged?page=0&size=2&sort=firstName,asc


Headers:

Accept: application/json

Response (200 OK) (example PageImpl JSON)
```
{
"content": [
{
"id": 1,
"firstName": "Sannee",
"lastName": "Kanupuru",
"email": "sannee@gmail.com",
"address": {
"id": 1,
"street": "Kothaguda",
"city": "Hyderabad",
"state": "TS",
"country": "India",
"zip": "500084"
},
"projectIds": [1]
},
{
"id": 2,
"firstName": "Bharath",
"lastName": "Kumar",
"email": "bharath@gmail.com",
"address": {
"id": 2,
"street": "Madhapur",
"city": "Hyderabad",
"state": "TS",
"country": "India",
"zip": "500081"
},
"projectIds": [1]
}
],
"pageable": {
"sort": { "sorted": true, "unsorted": false, "empty": false },
"pageNumber": 0,
"pageSize": 2,
"offset": 0,
"paged": true,
"unpaged": false
},
"totalPages": 1,
"totalElements": 2,
"last": true,
"size": 2,
"number": 0,
"sort": { "sorted": true, "unsorted": false, "empty": false },
"numberOfElements": 2,
"first": true,
"empty": false
}
```

Notes: findByProjectsId(projectId, Pageable) returns a Page<Employee>. Controller maps content to EmployeeDto and wraps back into a PageImpl to preserve pagination metadata.

## SCENARIO 7 — LAZY vs EAGER (how to test & expected SQL)

Goal: Compare SQL statements generated with Project.employees set to LAZY vs EAGER.

How to test (Postman requests):

Ensure SQL logging enabled in application.properties:
```
spring.jpa.show-sql=true
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

Call one of these endpoints in Postman:

GET http://localhost:8080/api/companies/name/TechCorp

GET http://localhost:8080/api/employees/1/eager

GET http://localhost:8080/api/employees/1/lazy

Observe console logs:

EAGER mapping on collections tends to fetch children either via immediate joins (one large query with duplicates) or extra selects depending on provider; you will likely see fewer round-trips for fetch-join but more duplicated rows.

LAZY with explicit fetch-join queries in repository (recommended) yields targeted joins only when you need them — the findByNameWithProjectsAndEmployees uses fetch-joins to get the full tree in a single HQL query.

Expected behavior in this project:

CompanyRepository.findByNameWithProjectsAndEmployees(name) uses HQL fetch-joins to get projects & employees in one query (recommended approach).

getEmployeeEager uses findByIdWithAddressAndProjects fetch-join — single joined SQL for employee + address + projects.

getEmployeeLazy issues a select for employee, then additional selects to initialize associations if accessed inside transaction.
