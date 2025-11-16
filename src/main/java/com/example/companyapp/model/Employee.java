package com.example.companyapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "employees", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Employee {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String firstName;
    private String lastName;

    @Column(nullable=false, unique=true)
    private String email;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;

    // We DON'T want to serialize the projects inside each employee (it would cause nested repetition)
    @ManyToMany(mappedBy = "employees", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Project> projects = new HashSet<>();

    public Employee() {}
    public Employee(String firstName, String lastName, String email) {
        this.firstName=firstName; this.lastName=lastName; this.email=email;
    }

    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String f) { this.firstName = f; }
    public String getLastName() { return lastName; }
    public void setLastName(String l) { this.lastName = l; }
    public String getEmail() { return email; }
    public void setEmail(String e) { this.email = e; }
    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }
    public Set<Project> getProjects() { return projects; }
    public void setProjects(Set<Project> projects) { this.projects = projects; }
}
