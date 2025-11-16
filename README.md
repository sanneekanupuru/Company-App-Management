# Company-App-Management
API list:

Base URL: http://localhost:8080/api

Scenario 1 — Entities & Employee ↔ Address (one-to-one)

Goal: Each Employee has one Address. Saving an employee also saves the address. Deleting an employee deletes their address.

1. Create employee (minimal fields)

POST /employees
Body (JSON):

{
  "firstName": "Sannee",
  "lastName": "Kanupuru",
  "email": "sannee@gmail.com"
}


Response: 201 Created — created employee JSON:

{
  "id": 1,
  "firstName": "Sannee",
  "lastName": "Kanupuru",
  "email": "sannee@gmail.com",
  "phone": null,
  "address": null
}

2. Create employee with address & phone (save address together)

POST /employees/details
Body:

{
  "firstName": "Bharath",
  "lastName": "Kumar",
  "email": "bharath@gmail.com",
  "phone": "9876543210",
  "address": {
    "street": "Madhapur",
    "city": "Hyderabad",
    "state": "TS",
    "country": "India",
    "zip": "500081"
  }
}


Response: 201 Created — employee JSON with nested address:

{
  "id": 2,
  "firstName": "Bharath",
  "lastName": "Kumar",
  "email": "bharath@gmail.com",
  "phone": "9876543210",
  "address": {
    "id": 2,
    "street": "Madhapur",
    "city": "Hyderabad",
    "state": "TS",
    "country": "India",
    "zip": "500081"
  }
}

3. Fetch employee by email (and see address)

GET /employees/email/{email}
Example:

GET /employees/email/sannee@gmail.com


Response: 200 OK

{
  "id": 1,
  "firstName": "Sannee",
  "lastName": "Kanupuru",
  "email": "sannee@gmail.com",
  "phone": null,
  "address": {
    "id": 1,
    "street": "Kothaguda",
    "city": "Hyderabad",
    "state": "TS",
    "country": "India",
    "zip": "500084"
  }
}


Lazy vs Eager:

If Employee.address uses FetchType.LAZY, the address is loaded only when JSON serialization accesses it (you may see additional SELECT).

If FetchType.EAGER, the address is loaded in the same query (or via join) when employee is fetched. You can try both to compare logs.

4. Delete employee by email (address removed automatically)

DELETE /employees/email/{email}
Example:

DELETE /employees/email/sannee@gmail.com


Responses:

204 No Content — deleted

404 Not Found — if email not found

Deleting employee will cascade and remove the linked address because @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true) is configured.

Scenario 2 — Project ⇄ Employee (many-to-many)

Goal: Each Project can have many Employees and vice-versa. Deleting a project does not delete employees.

1. Get all employees for a project

GET /projects/{projectId}/employees
Example:

GET /projects/1/employees


Response 200 OK with list:

[
  {
    "id": 1,
    "firstName": "Sannee",
    "lastName": "Kanupuru",
    "email": "sannee@gmail.com",
    "address": { ... }
  },
  {
    "id": 2,
    "firstName": "Bharath",
    "lastName": "Kumar",
    "email": "bharath@gmail.com",
    "address": { ... }
  }
]

2. Get all projects for an employee

GET /employees/{employeeId}/projects
Example:

GET /employees/1/projects


Response 200 OK:

[
  {
    "id": 1,
    "name": "Alpha",
    "company": { "id": 1, "name": "TechCorp" }
  }
]

3. Delete a project (employees remain)

DELETE /projects/{id} (You may need to implement if not already)

Should remove project and project_employees rows.

Employees remain untouched.

Scenario 3 — Company → Projects → Employees cascade delete

Goal: Company has many projects, each project many employees. When deleting the company, everything under it (projects, employees, addresses) should be deleted automatically (cascade).

1. Fetch company by name with projects and employees

GET /companies/name/{name}
Example:

GET /companies/name/TechCorp


Response 200 OK:

{
  "id": 1,
  "name": "TechCorp",
  "projects": [
    {
      "id": 1,
      "name": "Alpha",
      "employees": [
        { "id": 1, "firstName": "Sannee", "email": "sannee@gmail.com", "address": { ... } },
        { "id": 2, "firstName": "Bharath", "email": "bharath@gmail.com", "address": { ... } }
      ]
    },
    {
      "id": 2,
      "name": "Beta",
      "employees": [
        { "id": 3, "firstName": "Alice", "email": "alice@gmail.com", "address": null }
      ]
    }
  ]
}


This endpoint uses a single HQL fetch-join query (left join fetch) to load company → projects → project employees in one call (so you only query parent table and joins pull children).

2. Delete company and everything below (cascade)

DELETE /companies/{id}/cascade
Example:

DELETE /companies/1/cascade


Response:

204 No Content on success.

Implementation uses cascade = CascadeType.ALL and orphanRemoval = true on Company.projects so deleting the company deletes projects, and because projects own relationships to employees and employee has cascade on address, the downstream entities remove as well. (Make sure your entity cascade settings match this requirement.)

Scenario 4 — Update Employee & Address together

Goal: Updating employee should also update the address record.

Update (one simple endpoint in this project)

PUT /employees/update
Body (example — using email to find employee):

{
  "email": "sannee@gmail.com",
  "lastName": "Challa",
  "phone": "9113322264",
  "address": {
    "street": "Kothaguda",
    "city": "Hyderabad",
    "state": "TS",
    "country": "India",
    "zip": "500084"
  }
}


Response 200 OK returns updated Employee JSON (including nested address).

Implementation hint: find employee by email, update fields, update nested address fields and save employee. Because @OneToOne(cascade = ALL) is set, saving employee persists address changes.

Scenario 5 — data.sql (preload dataset) 

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

