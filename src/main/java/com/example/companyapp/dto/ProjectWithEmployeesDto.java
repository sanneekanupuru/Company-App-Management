package com.example.companyapp.dto;

import java.util.List;

public class ProjectWithEmployeesDto {
    private Long id;
    private String name;
    private List<EmployeeDto> employees;

    public ProjectWithEmployeesDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<EmployeeDto> getEmployees() { return employees; }
    public void setEmployees(List<EmployeeDto> employees) { this.employees = employees; }
}
