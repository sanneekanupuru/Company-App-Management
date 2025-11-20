package com.example.companyapp.controller;

import com.example.companyapp.dto.CompanyDto;
import com.example.companyapp.dto.EmployeeDto;
import com.example.companyapp.dto.ProjectDto;
import com.example.companyapp.dto.CompanyMapper;
import com.example.companyapp.dto.ProjectMapper;
import com.example.companyapp.model.Company;
import com.example.companyapp.model.Employee;
import com.example.companyapp.model.Project;
import com.example.companyapp.service.CompanyService;
import com.example.companyapp.service.EmployeeService;
import com.example.companyapp.service.ProjectService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class CompanyController {

    private final CompanyService companyService;
    private final ProjectService projectService;
    private final EmployeeService employeeService;

    public CompanyController(CompanyService companyService, ProjectService projectService, EmployeeService employeeService) {
        this.companyService = companyService;
        this.projectService = projectService;
        this.employeeService = employeeService;
    }

    // Scenario 2: Fetch employees working on a project (returns EmployeeDto list)
    @GetMapping("/projects/{projectId}/employees")
    public ResponseEntity<?> getEmployeesByProject(@PathVariable Long projectId) {
        List<Employee> list = projectService.getEmployeesByProject(projectId);
        if (list.isEmpty()) return ResponseEntity.notFound().build();

        List<EmployeeDto> dtos = list.stream()
                .map(com.example.companyapp.dto.EmployeeMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // Scenario 2: Fetch projects assigned to an employee (returns ProjectDto list)
    @GetMapping("/employees/{employeeId}/projects")
    public ResponseEntity<?> getProjectsByEmployee(@PathVariable Long employeeId) {
        List<Project> list = employeeService.getProjectsForEmployee(employeeId);
        if (list.isEmpty()) return ResponseEntity.notFound().build();

        List<ProjectDto> dtos = list.stream().map(ProjectMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // Scenario 3 (and 4 read): Fetch company details with projects and employees using HQL fetch join
    @GetMapping("/companies/name/{name}")
    public ResponseEntity<?> getCompanyTree(@PathVariable String name) {
        Company c = companyService.getCompanyWithTree(name);
        if (c == null) return ResponseEntity.notFound().build();
        CompanyDto dto = CompanyMapper.toDto(c);
        return ResponseEntity.ok(dto);
    }

    // Delete company cascade (Scenario 3)
    @DeleteMapping("/companies/{id}/cascade")
    public ResponseEntity<?> deleteCompanyCascade(@PathVariable Long id) {
        boolean ok = companyService.deleteCompanyCascade(id);
        return ok ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // Delete company but keep children (Scenario 4)
    @DeleteMapping("/companies/{id}/keep")
    public ResponseEntity<?> deleteCompanyKeep(@PathVariable Long id) {
        boolean ok = companyService.deleteCompanyKeepChildren(id);
        return ok ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // Pagination example: employees of project with pagination & sorting (Scenario 6)
    @GetMapping("/projects/{projectId}/employees/paged")
    public ResponseEntity<?> getEmployeesPaged(@PathVariable Long projectId,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size,
                                               @RequestParam(defaultValue = "id,asc") String sort) {
        String[] parts = sort.split(",");
        String prop = parts[0];
        Sort.Direction dir = parts.length>1 ? Sort.Direction.fromString(parts[1]) : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, prop));
        Page<Employee> p = projectService.getEmployeesByProjectPaged(projectId, pageable);
        if (p.isEmpty()) return ResponseEntity.notFound().build();

        // map page content to DTOs
        List<com.example.companyapp.dto.EmployeeDto> dtoContent = p.getContent().stream()
                .map(com.example.companyapp.dto.EmployeeMapper::toDto)
                .collect(Collectors.toList());

        // repackage into a simple response object (you can return Page-like structure if needed)
        return ResponseEntity.ok(new org.springframework.data.domain.PageImpl<>(dtoContent, pageable, p.getTotalElements()));
    }
}
