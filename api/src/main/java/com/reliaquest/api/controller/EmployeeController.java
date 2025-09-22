package com.reliaquest.api.controller;

import com.reliaquest.api.model.CreateEmployeeDto;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.IEmployeeService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmployeeController implements IEmployeeController<Employee, CreateEmployeeDto> {

    @Autowired
    IEmployeeService employeeService;

    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(this.employeeService.getAllEmployees());
    }

    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        return ResponseEntity.ok(this.employeeService.getEmployeesByNameSearch(searchString));
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(String id) {
        return ResponseEntity.ok(this.employeeService.getEmployeeById(id));
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        Integer highestSalary = this.employeeService.getHighestSalaryOfEmployees();
        if (highestSalary == null || highestSalary == -1)
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        else return ResponseEntity.ok(highestSalary);
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        return ResponseEntity.ok(this.employeeService.getTopTenHighestEarningEmployeeNames());
    }

    @Override
    public ResponseEntity<Employee> createEmployee(CreateEmployeeDto input) {
        return ResponseEntity.ok(this.employeeService.createEmployee(input));
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {
        return ResponseEntity.ok(this.employeeService.deleteEmployeeById(id));
    }
}
