package com.example.companyapp.dto;

import com.example.companyapp.model.Address;
import com.example.companyapp.model.Employee;

import java.util.stream.Collectors;

public class EmployeeMapper {

    public static EmployeeDto toDto(Employee e) {
        if (e == null) return null;
        EmployeeDto dto = new EmployeeDto();
        dto.setId(e.getId());
        dto.setFirstName(e.getFirstName());
        dto.setLastName(e.getLastName());
        dto.setEmail(e.getEmail());

        Address a = e.getAddress();
        if (a != null) {
            AddressDto ad = new AddressDto();
            ad.setId(a.getId());
            ad.setStreet(a.getStreet());
            ad.setCity(a.getCity());
            ad.setState(a.getState());
            ad.setCountry(a.getCountry());
            ad.setZip(a.getZip());
            dto.setAddress(ad);
        } else {
            dto.setAddress(null);
        }

        // Populate projectIds (safely assumes projects collection is initialized by service)
        if (e.getProjects() != null && !e.getProjects().isEmpty()) {
            dto.setProjectIds(e.getProjects().stream()
                    .map(p -> p.getId())
                    .collect(Collectors.toSet()));
        } else {
            dto.setProjectIds(null); // or Collections.emptySet() if you prefer []
        }

        return dto;
    }

    public static void updateEntityFromDto(Employee entity, EmployeeDto dto) {
        if (dto.getFirstName() != null) entity.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) entity.setLastName(dto.getLastName());
        if (dto.getEmail() != null) entity.setEmail(dto.getEmail());

        if (dto.getAddress() != null) {
            if (entity.getAddress() == null) {
                entity.setAddress(new com.example.companyapp.model.Address());
            }
            com.example.companyapp.model.Address a = entity.getAddress();
            AddressDto ad = dto.getAddress();
            if (ad.getStreet() != null) a.setStreet(ad.getStreet());
            if (ad.getCity() != null) a.setCity(ad.getCity());
            if (ad.getState() != null) a.setState(ad.getState());
            if (ad.getCountry() != null) a.setCountry(ad.getCountry());
            if (ad.getZip() != null) a.setZip(ad.getZip());
        } else {
            // if dto.address is null you may want to leave existing address or set to null depending on behavior
        }
    }
}
