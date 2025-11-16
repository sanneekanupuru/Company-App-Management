package com.example.companyapp.repository;

import com.example.companyapp.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    // HQL fetch join - fetch company with projects and project employees in one query
    @Query("select distinct c from Company c " +
            "left join fetch c.projects p " +
            "left join fetch p.employees e " +
            "where lower(c.name) = lower(:name)")
    Company findByNameWithProjectsAndEmployees(@Param("name") String name);

    Company findByNameIgnoreCase(String name);
}
