package com.reliaquest.api.service.impl;

import com.reliaquest.api.client.MockServerClient;
import com.reliaquest.api.exception.ApiException;
import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.DeleteEmployeeInput;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.IEmployeeService;
import io.micrometer.common.util.StringUtils;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmployeeService implements IEmployeeService {

    public static final int MAX_SIZE = 10;
    public static final String EMPLOYEE_NOT_FOUND = "employee_not_found";
    public static final String EMPLOYEE_DELETE_FAILED = "employee_delete_failed";
    public static final String INVALID_SEARCH_STRING = "invalid_search_string";
    public static final String INVALID_SEARCH_STRING_ERROR_MESSAGE =
            "The search string cannot be empty or contain only whitespace";
    public static final String INVALID_EMPLOYEE_ID = "invalid_employee_id";
    public static final String EMPLOYEE_ID_CANNOT_BE_EMPTY = "The employee ID cannot be empty";
    public static final String INVALID_ID_FORMAT = "The employee ID provided is not a valid UUID format";

    @Autowired
    MockServerClient mockServerClient;

    public List<Employee> getAllEmployees() {
        // Improvement: Paginated response to limit data transferred over network
        // provided - mock server supports pagination
        return this.mockServerClient.getAllEmployees();
    }

    public List<Employee> getEmployeesByNameSearch(String searchString) {

        // Validating the input string before making any API calls.
        if (StringUtils.isBlank(searchString)) {
            throw new ApiException(
                    INVALID_SEARCH_STRING, INVALID_SEARCH_STRING_ERROR_MESSAGE, HttpStatus.BAD_REQUEST.value());
        }

        List<Employee> employees = this.mockServerClient.getAllEmployees();
        ;

        /* The filter() operation returns a new stream containing only the elements that match the given predicate.
         * The predicate here is a lambda expression that checks if the employee's name contains the search string,
         * after converting both to lowercase for a case-insensitive match.
         *
         * The collect() operation is a terminal operation that gathers the elements of the stream
         * into a new collection, in this case, a List.
         */
        return employees.stream()
                .filter(employee -> employee.getEmployeeName().toLowerCase().contains(searchString.toLowerCase()))
                .collect(Collectors.toList());
    }

    public Employee getEmployeeById(String id) {

        // Validate the input string before making any API calls.
        if (StringUtils.isBlank(id)) {
            throw new ApiException(INVALID_EMPLOYEE_ID, EMPLOYEE_ID_CANNOT_BE_EMPTY, HttpStatus.BAD_REQUEST.value());
        }

        // Validate if the string is a valid UUID.
        try {
            UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new ApiException(INVALID_EMPLOYEE_ID, INVALID_ID_FORMAT, HttpStatus.BAD_REQUEST.value());
        }

        return this.mockServerClient.getEmployeeById(id);
    }

    public Integer getHighestSalaryOfEmployees() {

        List<Employee> employees = this.mockServerClient.getAllEmployees();

        Optional<Employee> highestPaidEmployee =
                employees.stream().max(Comparator.comparingInt(Employee::getEmployeeSalary));

        Employee toRet = highestPaidEmployee.orElse(null);

        if (toRet != null) return toRet.getEmployeeSalary();
        else return -1;
    }

    public List<String> getTopTenHighestEarningEmployeeNames() {
        List<Employee> employees = this.mockServerClient.getAllEmployees();

        return employees.stream()
                .sorted(Comparator.comparingLong(Employee::getEmployeeSalary).reversed())
                .limit(MAX_SIZE)
                .map(Employee::getEmployeeName)
                .toList();
    }

    public Employee createEmployee(CreateEmployeeInput input) {
        return this.mockServerClient.createEmployee(input);
    }


    /*
     * Deletes a unique employee by their ID.
     *
     * This method first retrieves all employees from the mock server to validate the request
     * and prevent a partial or ambiguous deletion.
     *
     * It performs the following steps:
     * 1. Finds the employee using the provided ID from the local list.
     * 2. If the employee is not found, it throws an ApiException with a BAD_REQUEST status.
     * 3. It then checks the entire list for other employees with the same name.
     * 4. If more than one employee shares the same name, it logs an error and throws a
     * CONFLICT status ApiException to prevent accidental deletion of a different employee.
     * 5. If the employee is unique, it proceeds with the deletion by name and returns
     * the name of the deleted employee.
     */
    @Synchronized
    public String deleteEmployeeById(String id) {
        List<Employee> allEmployees = this.mockServerClient.getAllEmployees();

        // Finding the employee with the ID from the already present list to prevent
        // invoking GET employee by id API again
        Optional<Employee> employeeToDelete = allEmployees.stream()
                .filter(employee -> employee.getId().equals(id))
                .findFirst();

        if (employeeToDelete.isEmpty())
            throw new ApiException(
                    EMPLOYEE_NOT_FOUND, "Invalid employee id: [%s]".formatted(id), HttpStatus.BAD_REQUEST.value());

        String employeeName = employeeToDelete.get().getEmployeeName();

        // Checking for duplicate names to prevent accidental deletion.
        long employeeCountWithSameName = allEmployees.stream()
                .filter(emp -> emp.getEmployeeName().equalsIgnoreCase(employeeName))
                .count();

        if (employeeCountWithSameName > 1) {
            log.error("Cannot delete employee with name [%s]. Multiple employees with this name exist"
                    .formatted(employeeName));
            throw new ApiException(
                    EMPLOYEE_DELETE_FAILED,
                    "Ambiguous deletion: multiple employees found with the same name",
                    HttpStatus.CONFLICT.value());
        }

        log.info("Found unique employee with ID [%s], proceeding with deletion by name: [%s]"
                .formatted(id, employeeName));

        this.mockServerClient.deleteEmployee(new DeleteEmployeeInput(employeeName));
        return employeeName;
    }
}
