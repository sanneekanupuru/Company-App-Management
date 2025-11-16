package com.example.companyapp.repository;

import com.example.companyapp.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByProjectsId(Long projectId);
    Page<Employee> findByProjectsId(Long projectId, Pageable pageable);
}
