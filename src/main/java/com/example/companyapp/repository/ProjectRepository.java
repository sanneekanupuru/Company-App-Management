package com.example.companyapp.repository;

import com.example.companyapp.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("select p from Project p left join fetch p.employees where p.id = :id")
    Project findByIdWithEmployees(Long id);
}
