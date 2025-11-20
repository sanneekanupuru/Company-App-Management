package com.example.companyapp.dto;

import com.example.companyapp.model.Project;

import java.util.List;
import java.util.stream.Collectors;

public class ProjectMapper {

    public static ProjectDto toDto(Project p) {
        if (p == null) return null;
        ProjectDto dto = new ProjectDto();
        dto.setId(p.getId());
        dto.setName(p.getName());
        return dto;
    }

    public static ProjectWithEmployeesDto toWithEmployeesDto(Project p) {
        if (p == null) return null;
        ProjectWithEmployeesDto dto = new ProjectWithEmployeesDto();
        dto.setId(p.getId());
        dto.setName(p.getName());
        if (p.getEmployees() != null && !p.getEmployees().isEmpty()) {
            List<EmployeeDto> emps = p.getEmployees().stream()
                    .map(EmployeeMapper::toDto) // reuses your existing EmployeeMapper.toDto
                    .collect(Collectors.toList());
            dto.setEmployees(emps);
        }
        return dto;
    }

    public static List<ProjectDto> toDtoList(List<Project> list) {
        return list.stream().map(ProjectMapper::toDto).collect(Collectors.toList());
    }
}
