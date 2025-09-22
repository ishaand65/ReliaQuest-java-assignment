package com.reliaquest.api.service;

import com.reliaquest.api.client.MockServerClient;
import com.reliaquest.api.exception.ApiException;
import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.impl.EmployeeService;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    private MockServerClient mockServerClient;

    @InjectMocks
    private IEmployeeService employeeService = new EmployeeService();

    private List<Employee> mockEmployees;

    @BeforeEach
    void setUp() {

        // Create a list of mock Employee objects
        mockEmployees = List.of(
                new Employee(
                        UUID.randomUUID().toString(),
                        "Liam Anderson",
                        95000,
                        28,
                        "Software Engineer",
                        "liam.a@test.com"),
                new Employee(
                        UUID.randomUUID().toString(),
                        "Olivia Chen",
                        120000,
                        35,
                        "Marketing Manager",
                        "olivia.c@test.com"),
                new Employee(
                        UUID.randomUUID().toString(),
                        "Ethan Miller",
                        80000,
                        25,
                        "Financial Analyst",
                        "ethan.m@test.com"),
                new Employee(
                        UUID.randomUUID().toString(),
                        "Isabella Garcia",
                        70000,
                        42,
                        "Human Resources Generalist",
                        "isabella.g@test.com"),
                new Employee(
                        UUID.randomUUID().toString(), "Noah White", 105000, 31, "UX/UI Designer", "noah.w@test.com"));
    }

    @Test
    void testGetAllEmployeesSuccess() {
        Mockito.when(mockServerClient.getAllEmployees()).thenReturn(mockEmployees);

        List<Employee> result = employeeService.getAllEmployees();

        Assertions.assertNotNull(result, "The result should not be null.");
        Assertions.assertEquals(
                mockEmployees.size(), result.size(), "The number of employees should match the mock data.");
        Assertions.assertEquals(
                mockEmployees.get(0).getEmployeeName(),
                result.get(0).getEmployeeName(),
                "The first employee's name should match.");
    }

    @Test
    void testGetEmployeesByNameSearchSuccess() {
        // Arrange: Mock the dependency to return our mock data.
        Mockito.when(mockServerClient.getAllEmployees()).thenReturn(mockEmployees);
        String searchString = "miller";

        // Act: Call the method with a search string.
        List<Employee> result = employeeService.getEmployeesByNameSearch(searchString);

        // Assert: Verify that the filter operation worked correctly.
        Assertions.assertNotNull(result, "The result should not be null.");
        Assertions.assertEquals(1, result.size(), "The search result should contain exactly one employee.");
        Assertions.assertEquals(
                "Ethan Miller", result.get(0).getEmployeeName(), "The employee name should match the search string.");
    }

    @Test
    void testGetEmployeesByNameSearchWithBlankInput() {
        // Arrange: Define a blank search string.
        String searchString = "   ";

        // Act & Assert: Verify that an ApiException is thrown for blank input.
        ApiException thrown = Assertions.assertThrows(
                ApiException.class,
                () -> {
                    employeeService.getEmployeesByNameSearch(searchString);
                },
                "An ApiException should be thrown for blank search strings.");

        Assertions.assertEquals(
                HttpStatus.BAD_REQUEST.value(), thrown.getHttpStatusCode(), "HTTP status code should be 400.");
    }

    @Test
    void testGetHighestSalaryOfEmployeesSuccess() {
        // Arrange: Mock the dependency to return our mock data.
        Mockito.when(mockServerClient.getAllEmployees()).thenReturn(mockEmployees);

        // Act: Call the method under test.
        Integer highestSalary = employeeService.getHighestSalaryOfEmployees();

        // Assert: Verify the result is the correct highest salary.
        Assertions.assertNotNull(highestSalary, "The highest salary should not be null.");
        Assertions.assertEquals(120000, highestSalary, "The highest salary should be 120000.");
    }

    @Test
    void testGetHighestSalaryOfEmployeesEmptyList() {
        // Arrange: Mock the dependency to return an empty list.
        Mockito.when(mockServerClient.getAllEmployees()).thenReturn(Collections.emptyList());

        // Act: Call the method under test.
        Integer highestSalary = employeeService.getHighestSalaryOfEmployees();

        // Assert: Verify that the method returns -1 for an empty list.
        Assertions.assertEquals(-1, highestSalary, "The result should be -1 for an empty list.");
    }

    @Test
    void testGetTopTenHighestEarningEmployeeNamesSuccess() {
        // Arrange: Mock the dependency to return our mock data.
        Mockito.when(mockServerClient.getAllEmployees()).thenReturn(mockEmployees);

        // Act: Call the method under test.
        List<String> topEmployeeNames = employeeService.getTopTenHighestEarningEmployeeNames();

        // Assert: Verify the names are in the correct order and the size is as expected.
        Assertions.assertNotNull(topEmployeeNames, "The result should not be null.");
        Assertions.assertEquals(
                mockEmployees.size(),
                topEmployeeNames.size(),
                "The size of the returned list should match the number of employees.");
        Assertions.assertEquals(
                "Olivia Chen", topEmployeeNames.get(0), "The highest paid employee should be Olivia Chen.");
        Assertions.assertEquals(
                "Noah White", topEmployeeNames.get(1), "The second highest paid employee should be Noah White.");
        Assertions.assertEquals(
                "Liam Anderson", topEmployeeNames.get(2), "The third highest paid employee should be Liam Anderson.");
        Assertions.assertEquals(
                "Ethan Miller", topEmployeeNames.get(3), "The fourth highest paid employee should be Ethan Miller.");
        Assertions.assertEquals(
                "Isabella Garcia",
                topEmployeeNames.get(4),
                "The fifth highest paid employee should be Isabella Garcia.");
    }

    @Test
    void testGetTopTenHighestEarningEmployeeNamesEmptyList() {
        // Arrange: Mock the dependency to return an empty list.
        Mockito.when(mockServerClient.getAllEmployees()).thenReturn(Collections.emptyList());

        // Act: Call the method under test.
        List<String> topEmployeeNames = employeeService.getTopTenHighestEarningEmployeeNames();

        // Assert: Verify that the method returns an empty list.
        Assertions.assertTrue(
                topEmployeeNames.isEmpty(), "The result should be an empty list for an empty employee list.");
    }

    @Test
    void testCreateEmployeeSuccess() {
        Employee newEmployee =
                new Employee(UUID.randomUUID().toString(), "Jane Doe", 75000, 30, "Analyst", "jane.doe@test.com");

        Mockito.when(mockServerClient.createEmployee(Mockito.any(CreateEmployeeInput.class)))
                .thenReturn(newEmployee);

        Employee createdEmployee =
                employeeService.createEmployee(new CreateEmployeeInput("Jane Doe", 75000, 30, "Analyst"));

        Assertions.assertNotNull(createdEmployee, "The created employee should not be null.");
        Assertions.assertEquals(
                newEmployee.getEmployeeName(),
                createdEmployee.getEmployeeName(),
                "The returned employee should match the mocked response.");

        Mockito.verify(mockServerClient, Mockito.times(1)).createEmployee(Mockito.any(CreateEmployeeInput.class));
    }
}
