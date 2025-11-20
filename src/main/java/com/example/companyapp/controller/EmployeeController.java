package com.example.companyapp.controller;

import com.example.companyapp.dto.EmployeeDto;
import com.example.companyapp.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // LAZY variant: loads employee normally and initializes address inside transactional method
    @GetMapping("/{id}/lazy")
    public ResponseEntity<EmployeeDto> getEmployeeLazy(@PathVariable Long id) {
        EmployeeDto dto = employeeService.getEmployeeLazy(id);
        return ResponseEntity.ok(dto);
    }

    // EAGER variant: uses fetch join query inside repository
    @GetMapping("/{id}/eager")
    public ResponseEntity<EmployeeDto> getEmployeeEager(@PathVariable Long id) {
        EmployeeDto dto = employeeService.getEmployeeEager(id);
        return ResponseEntity.ok(dto);
    }

    // Update employee (Scenario 4)
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDto> updateEmployee(@PathVariable Long id, @RequestBody EmployeeDto dto) {
        EmployeeDto updated = employeeService.updateEmployee(id, dto);
        return ResponseEntity.ok(updated);
    }

    // Delete employee (Scenario 1: address should be deleted due to cascade/orphanRemoval)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
