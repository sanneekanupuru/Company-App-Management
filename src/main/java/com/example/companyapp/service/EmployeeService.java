package com.example.companyapp.service;

import com.example.companyapp.model.Project;
import com.example.companyapp.model.Employee;
import com.example.companyapp.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepo;

    public EmployeeService(EmployeeRepository employeeRepo) {
        this.employeeRepo = employeeRepo;
    }

    public List<Project> getProjectsForEmployee(Long employeeId) {
        Optional<Employee> opt = employeeRepo.findById(employeeId);
        return opt.map(e -> List.copyOf(e.getProjects())).orElse(List.of());
    }
}
