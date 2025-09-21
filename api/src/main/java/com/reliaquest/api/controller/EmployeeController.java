package com.reliaquest.api.controller;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.reliaquest.api.MockServerClient;
import com.reliaquest.api.dto.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmployeeController implements IEmployeeController <Employee, Object> {

    @Autowired MockServerClient mockServerClient;

    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() {
        try {
            List<Employee> employees = this.mockServerClient.getAllEmployee();
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            // TODO: use logger
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {

        // TODO: input string validation

        String polishedSearchString = searchString.trim();
        List<Employee> employees;

        try {
            employees = this.mockServerClient.getAllEmployee();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        /* The filter() operation returns a new stream containing only the elements that match the given predicate.
         * The predicate here is a lambda expression that checks if the employee's name contains the search string,
         * after converting both to lowercase for a case-insensitive match.
         *
         * The collect() operation is a terminal operation that gathers the elements of the stream
         * into a new collection, in this case, a List.
         */
        return ResponseEntity.ok(employees.stream()
                .filter(employee -> employee.getEmployeeName().toLowerCase().contains(polishedSearchString.toLowerCase()))
                .collect(Collectors.toList()));
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(String id) {

        // TODO: input string validation

        try {
            Employee employee = this.mockServerClient.getEmployeeById(id);
            return ResponseEntity.ok(employee);
        } catch (Exception e) {
            // TODO: use logger
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {

        List<Employee> employees;

        try {
            employees = this.mockServerClient.getAllEmployee();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        Optional<Employee> highestPaidEmployee = employees.stream()
                .max(Comparator.comparingInt(Employee::getEmployeeSalary));

        Employee toRet = highestPaidEmployee.orElse(null);

        if (toRet != null) return ResponseEntity.ok(toRet.getEmployeeSalary());
        else return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        List<Employee> employees;

        try {
            employees = this.mockServerClient.getAllEmployee();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        List<String> topEmployeeNames = employees.stream()
                .sorted(Comparator.comparingInt(Employee::getEmployeeSalary).reversed())
                .limit(10)
                .map(Employee::getEmployeeName)
                .toList();

        return ResponseEntity.ok(topEmployeeNames);
    }

    @Override
    public ResponseEntity<Employee> createEmployee(Object employeeInput) {
        return null;
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {
        return null;
    }
}
