package com.example.companyapp.service;

import com.example.companyapp.dto.EmployeeDto;
import com.example.companyapp.dto.EmployeeMapper;
import com.example.companyapp.exception.ResourceNotFoundException;
import com.example.companyapp.model.Employee;
import com.example.companyapp.model.Project;
import com.example.companyapp.repository.EmployeeRepository;
import com.example.companyapp.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * EmployeeService — updated to remove project associations using JPA/HQL only.
 */
@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepo;
    private final ProjectRepository projectRepo;

    public EmployeeService(EmployeeRepository employeeRepo, ProjectRepository projectRepo) {
        this.employeeRepo = employeeRepo;
        this.projectRepo = projectRepo;
    }

    /**
     * Lazy variant: loads employee normally and initializes address and projects inside the transaction
     * so mapping to DTO will include projectIds.
     */
    @Transactional(readOnly = true)
    public EmployeeDto getEmployeeLazy(Long id) {
        // find normally (address lazy).
        Employee e = employeeRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id " + id));
        // touch address to initialize (if exists)
        if (e.getAddress() != null) {
            e.getAddress().getId(); // initialize proxy
        }
        // initialize projects collection inside transaction so mapper can read project ids
        e.getProjects().size();
        return EmployeeMapper.toDto(e);
    }

    /**
     * Eager variant: uses fetch join query (single query) to fetch address + projects
     */
    @Transactional(readOnly = true)
    public EmployeeDto getEmployeeEager(Long id) {
        Employee e = employeeRepo.findByIdWithAddressAndProjects(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id " + id));
        // projects and address are already initialized by fetch join
        return EmployeeMapper.toDto(e);
    }

    /**
     * Delete employee without using native SQL.
     *
     * Steps:
     * 1) Load employee using fetch-join (address + projects) — single HQL query.
     * 2) Remove this employee from each project's employees set (the owning side).
     * 3) Save the modified projects so Hibernate will issue deletes on the join table.
     * 4) Delete the employee entity (address removed via cascade/orphanRemoval).
     */
    @Transactional
    public void deleteEmployee(Long id) {
        Employee e = employeeRepo.findByIdWithAddressAndProjects(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id " + id));

        Set<Project> projects = e.getProjects();

        if (projects != null && !projects.isEmpty()) {
            // Remove employee from owning side and collect updated projects
            List<Project> updated = projects.stream()
                    .peek(p -> p.getEmployees().remove(e))
                    .collect(Collectors.toList());

            // Persist changes to owning side so join rows are deleted
            projectRepo.saveAll(updated);
        }

        // Now safe to delete employee. Address will be removed because of cascade + orphanRemoval on Employee.address
        employeeRepo.delete(e);
    }

    @Transactional
    public EmployeeDto updateEmployee(Long id, EmployeeDto dto) {
        Employee e = employeeRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id " + id));
        EmployeeMapper.updateEntityFromDto(e, dto);
        Employee saved = employeeRepo.save(e);
        // ensure projects collection is available when converting to DTO; use saved entity
        saved.getProjects().size();
        return EmployeeMapper.toDto(saved);
    }

    public List<Project> getProjectsForEmployee(Long employeeId) {
        Optional<Employee> opt = employeeRepo.findById(employeeId);
        return opt.map(e -> List.copyOf(e.getProjects())).orElse(List.of());
    }
}
