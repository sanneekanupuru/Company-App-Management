package com.example.companyapp.dto;

import java.util.Set;

public class EmployeeDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private AddressDto address;
    private Set<Long> projectIds; // optional helper to show assignments

    public EmployeeDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public AddressDto getAddress() { return address; }
    public void setAddress(AddressDto address) { this.address = address; }
    public Set<Long> getProjectIds() { return projectIds; }
    public void setProjectIds(Set<Long> projectIds) { this.projectIds = projectIds; }
}
