package com.example.companyapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "addresses")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Address {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String street;
    private String city;
    private String state;
    private String country;
    private String zip;

    @OneToOne(mappedBy = "address", fetch = FetchType.LAZY)
    @JsonIgnore
    private Employee employee;

    public Address() {}
    public Address(String street, String city, String state, String country, String zip) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.country = country;
        this.zip = zip;
    }

    public Long getId() { return id; }
    public String getStreet() { return street; }
    public void setStreet(String s) { this.street = s; }
    public String getCity() { return city; }
    public void setCity(String c) { this.city = c; }
    public String getState() { return state; }
    public void setState(String s) { this.state = s; }
    public String getCountry() { return country; }
    public void setCountry(String c) { this.country = c; }
    public String getZip() { return zip; }
    public void setZip(String z) { this.zip = z; }
    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee e) { this.employee = e; }
}
