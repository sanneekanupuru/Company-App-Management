package com.example.companyapp.service;

import com.example.companyapp.model.Employee;
import com.example.companyapp.model.Project;
import com.example.companyapp.repository.EmployeeRepository;
import com.example.companyapp.repository.ProjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {
    private final ProjectRepository projectRepo;
    private final EmployeeRepository employeeRepo;

    public ProjectService(ProjectRepository projectRepo, EmployeeRepository employeeRepo) {
        this.projectRepo = projectRepo;
        this.employeeRepo = employeeRepo;
    }

    public List<Employee> getEmployeesByProject(Long projectId) {
        return employeeRepo.findByProjectsId(projectId);
    }

    public Page<Employee> getEmployeesByProjectPaged(Long projectId, Pageable pageable) {
        return employeeRepo.findByProjectsId(projectId, pageable);
    }

    public Project getProjectWithEmployees(Long id) {
        return projectRepo.findByIdWithEmployees(id);
    }
}
