package com.example.companyapp.repository;

import com.example.companyapp.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // existing methods
    List<Employee> findByProjectsId(Long projectId);
    Page<Employee> findByProjectsId(Long projectId, Pageable pageable);

    // EAGER fetch of address using JPQL fetch join
    @Query("select e from Employee e left join fetch e.address where e.id = :id")
    Optional<Employee> findByIdWithAddress(@Param("id") Long id);

    // NEW: fetch address AND projects (initializes projects collection in same query)
    @Query("select distinct e from Employee e " +
            "left join fetch e.address " +
            "left join fetch e.projects p " +
            "where e.id = :id")
    Optional<Employee> findByIdWithAddressAndProjects(@Param("id") Long id);
}
