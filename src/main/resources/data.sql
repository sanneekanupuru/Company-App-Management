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
