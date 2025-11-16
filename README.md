Company App Management API

A Spring Boot + JPA/Hibernate application demonstrating One-to-Many, Many-to-Many, and One-to-One relationships with properly structured JSON responses.

📌 Entity Relationships

Company → Projects : One-to-Many

Project → Employees : Many-to-Many

Employee → Address : One-to-One (Cascade ALL, orphan removal)

⭐ API ENDPOINTS WITH REQUEST & RESPONSE EXAMPLES

1️⃣ COMPANY APIs
➤ Create Company

POST /api/companies

Request:

{
  "name": "TechCorp"
}


Response:

{
  "id": 1,
  "name": "TechCorp",
  "projects": []
}

➤ Get Company by ID

GET /api/companies/1

Response:

{
  "id": 1,
  "name": "TechCorp",
  "projects": []
}

➤ Get Company by Name

GET /api/companies/name/TechCorp

Response:

{
  "id": 1,
  "name": "TechCorp",
  "projects": [
    {
      "id": 1,
      "name": "Alpha",
      "employees": [
        { "id": 1, "firstName": "Sannee", "email": "sannee@gmail.com" },
        { "id": 2, "firstName": "Bharath", "email": "bharath@gmail.com" }
      ]
    },
    {
      "id": 2,
      "name": "Beta",
      "employees": [
        { "id": 3, "firstName": "Alice", "email": "alice@gmail.com" }
      ]
    }
  ]
}

➤ Get All Companies

GET /api/companies

Response:

[
  {
    "id": 1,
    "name": "TechCorp"
  }
]

➤ Delete Company

DELETE /api/companies/1

Response:

204 No Content

2️⃣ PROJECT APIs
➤ Create Project for a Company

POST /api/projects/company/1

Request:

{
  "name": "Alpha"
}


Response:

{
  "id": 1,
  "name": "Alpha",
  "employees": []
}

➤ Get Project by ID

GET /api/projects/1

Response:

{
  "id": 1,
  "name": "Alpha",
  "employees": []
}

➤ Assign Employee to Project

POST /api/projects/1/employees/3

Response:

{
  "projectId": 1,
  "employeeId": 3,
  "status": "Employee added to project"
}

➤ Remove Employee from Project

DELETE /api/projects/1/employees/3

Response:

{
  "projectId": 1,
  "employeeId": 3,
  "status": "Employee removed from project"
}

3️⃣ EMPLOYEE APIs
➤ Create Employee

POST /api/employees

Request:

{
  "firstName": "Sannee",
  "lastName": "Kanupuru",
  "email": "sannee@gmail.com"
}


Response:

{
  "id": 1,
  "firstName": "Sannee",
  "lastName": "Kanupuru",
  "email": "sannee@gmail.com",
  "address": null
}

➤ Get Employee by ID

GET /api/employees/1

Response:

{
  "id": 1,
  "firstName": "Sannee",
  "lastName": "Kanupuru",
  "email": "sannee@gmail.com",
  "address": null
}

➤ Get All Employees

GET /api/employees

Response:

[
  {
    "id": 1,
    "firstName": "Sannee",
    "email": "sannee@gmail.com"
  }
]

➤ Delete Employee

DELETE /api/employees/1

Response:

204 No Content


(Also deletes Address automatically because of cascade + orphanRemoval).

4️⃣ ADDRESS APIs
➤ Assign Address to Employee

POST /api/addresses/employee/1

Request:

{
  "street": "Kothaguda",
  "city": "Hyderabad",
  "state": "TS",
  "country": "India",
  "zip": "500084"
}


Response:

{
  "employeeId": 1,
  "address": {
    "street": "Kothaguda",
    "city": "Hyderabad",
    "state": "TS",
    "country": "India",
    "zip": "500084"
  }
}

➤ Update Employee Address

PUT /api/addresses/employee/1

Request:

{
  "street": "Madhapur",
  "city": "Hyderabad",
  "state": "TS",
  "country": "India",
  "zip": "500081"
}


Response:

{
  "employeeId": 1,
  "message": "Address updated successfully"
}

➤ Delete Employee Address

DELETE /api/addresses/employee/1

Response:

204 No Content
