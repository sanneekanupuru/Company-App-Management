package com.example.companyapp.service;

import com.example.companyapp.model.Company;
import com.example.companyapp.repository.CompanyRepository;
import com.example.companyapp.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompanyService {
    private final CompanyRepository companyRepo;
    private final ProjectRepository projectRepo;

    public CompanyService(CompanyRepository companyRepo, ProjectRepository projectRepo) {
        this.companyRepo = companyRepo;
        this.projectRepo = projectRepo;
    }

    public Company getCompanyWithTree(String name) {
        return companyRepo.findByNameWithProjectsAndEmployees(name);
    }

    @Transactional
    public boolean deleteCompanyCascade(Long id) {
        if (!companyRepo.existsById(id)) return false;
        companyRepo.deleteById(id);
        return true;
    }

    @Transactional
    public boolean deleteCompanyKeepChildren(Long id) {
        return companyRepo.findById(id).map(company -> {
            company.getProjects().forEach(p -> p.setCompany(null));
            company.getProjects().clear();
            companyRepo.delete(company);
            return true;
        }).orElse(false);
    }
}
