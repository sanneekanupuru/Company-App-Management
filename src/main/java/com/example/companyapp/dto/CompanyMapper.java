package com.example.companyapp.dto;

import com.example.companyapp.model.Company;

import java.util.List;
import java.util.stream.Collectors;

public class CompanyMapper {

    public static CompanyDto toDto(Company c) {
        if (c == null) return null;
        CompanyDto dto = new CompanyDto();
        dto.setId(c.getId());
        dto.setName(c.getName());

        if (c.getProjects() != null && !c.getProjects().isEmpty()) {
            // map each project to ProjectWithEmployeesDto
            List<ProjectWithEmployeesDto> projects = c.getProjects().stream()
                    .map(ProjectMapper::toWithEmployeesDto)
                    .collect(Collectors.toList());
            dto.setProjects(projects);
        }
        return dto;
    }
}
