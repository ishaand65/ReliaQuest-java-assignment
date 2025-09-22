package com.reliaquest.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.reactive.server.WebTestClient.bindToServer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

/**
 * This test class performs a full-stack integration test of the application.
 * It acts like an external client (e.g., Postman) by making a real HTTP request
 * to the API server. This verifies that the API server is up, and that it can
 * successfully communicate with its downstream services (the mock server).
 *
 * NOTE: For this test to pass, both the API server and the mock server must be running
 * and accessible at their configured ports.
 *
 * NOTE: Rate limiting on the mock server side has to be excluded for all tests to run
 *
 * This test has been excluded from gradle build
 */
class ApiApplicationTest {

    private final WebTestClient webTestClient =
            bindToServer().baseUrl("http://localhost:8111").build();

    /**
     * Test case to verify that the API returns a list of employees.
     * This test ensures that the full request pipeline is working:
     * Client -> API Server (localhost:8111) -> Mock Server (localhost:8112)
     */
    @Test
    void testGetAllEmployeesApiReturnsData() {
        webTestClient
                .get()
                .uri("/")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.OK)
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$")
                .isArray(); // Assert that the response body is a JSON array
    }

    /**
     * This test case simulates a multi-step API workflow.
     * It first calls the getAllEmployees API, extracts the name of the first employee,
     * and then uses that name to test the search API.
     */
    @Test
    void testSearchApiWithExistingEmployeeName() throws JsonProcessingException {
        String allEmployeesJson = webTestClient
                .get()
                .uri("/")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.OK)
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode employeesNode = objectMapper.readTree(allEmployeesJson);

        assertTrue(employeesNode.isArray() && !employeesNode.isEmpty(), "Expected a non-empty list of employees.");
        String employeeName = employeesNode.get(0).get("employee_name").asText();

        webTestClient
                .get()
                .uri("/search/{searchName}", employeeName)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.OK)
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].employee_name")
                .isEqualTo(employeeName); // Assert the name in the response
    }

    /**
     * This test simulates another multi-step API workflow.
     * It first calls the getAllEmployees API, extracts the ID of the first employee,
     * and then uses that ID to test the specific employee details API.
     */
    @Test
    void testGetEmployeeByIdApiWithExistingId() throws JsonProcessingException {
        // Step 1: Calling the /employees API to get the full list of employees.
        String allEmployeesJson = webTestClient
                .get()
                .uri("/")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.OK)
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        // Using ObjectMapper to parse the JSON string.
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode employeesNode = objectMapper.readTree(allEmployeesJson);

        assertTrue(employeesNode.isArray() && !employeesNode.isEmpty(), "Expected a non-empty list of employees.");
        JsonNode firstEmployee = employeesNode.get(0);
        String employeeId = firstEmployee.get("id").asText();
        String expectedEmployeeJson = firstEmployee.toString();

        // Step 2: Using the extracted ID to call the /employees/{id} API.
        webTestClient
                .get()
                .uri("/{employee_id}", employeeId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.OK)
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json(expectedEmployeeJson); // Assert that the response body matches the expected JSON string.
    }

    /**
     * Test case to verify that the /highestSalary API returns a single integer.
     */
    @Test
    void testHighestSalaryApiReturnsInteger() {
        webTestClient
                .get()
                .uri("/highestSalary")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.OK)
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody(Integer.class)
                .consumeWith(response -> {
                    // Get the returned integer value.
                    Integer highestSalary = response.getResponseBody();

                    // Assert that the body is not null and is a positive integer.
                    assertNotNull(highestSalary, "Expected a non-null integer response.");
                    assertTrue(highestSalary > 0, "Expected the highest salary to be a positive value.");
                });
    }

    /**
     * Test case to verify that the /topTenHighestEarningEmployeeNames API
     * returns a JSON array of exactly 10 strings.
     */
    @Test
    void testTopTenHighestEarningEmployeeNamesApi() {
        webTestClient
                .get()
                .uri("/topTenHighestEarningEmployeeNames")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.OK)
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$")
                .isArray() // Assert that the response is a JSON array
                .jsonPath("$.length()")
                .isEqualTo(10); // Assert that the array has a length of 10
    }

    /**
     * Test case to verify that the API successfully creates a new employee via a POST request.
     */
    @Test
    void testCreateEmployeeApi() {
        // Creating the request body payload as a Map.
        Map<String, Object> requestBody =
                Map.of("name", "Amelia Dsouza", "salary", 98123, "age", 60, "title", "Vice President");

        // Performing the POST request and validate the response.
        webTestClient
                .post()
                .uri("/")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(requestBody), Map.class)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.OK)
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id")
                .exists() // Assert that an ID is generated and present
                .jsonPath("$.employee_name")
                .isEqualTo("Amelia Dsouza") // Assert the name is correct
                .jsonPath("$.employee_salary")
                .isEqualTo(98123) // Assert the salary is correct
                .jsonPath("$.employee_age")
                .isEqualTo(60) // Assert the age is correct
                .jsonPath("$.employee_title")
                .isEqualTo("Vice President"); // Assert the title is correct
    }

    /**
     * Test case to verify the complete create-and-delete flow for an employee.
     * It first creates a new employee, then uses the returned ID to delete it.
     */
    @Test
    void testDeleteEmployeeAfterCreation() throws JsonProcessingException {
        // Step 1: Creating a new employee.
        Map<String, Object> requestBody =
                Map.of("name", "Temporary Employee", "salary", 100000, "age", 30, "title", "Temporary Title");

        // Performing the POST request and capture the response body.
        String responseBody = webTestClient
                .post()
                .uri("/")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(requestBody), Map.class)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.OK)
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        // Parsing the JSON response to get the employee's ID.
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode createdEmployee = objectMapper.readTree(responseBody);
        String employeeId = createdEmployee.get("id").asText();
        String employeeName = createdEmployee.get("employee_name").asText();

        // Step 2: Deleting the newly created employee using its ID.
        webTestClient
                .delete()
                .uri("/{id}", employeeId)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.OK)
                .expectBody(String.class)
                .isEqualTo(employeeName); // Assert that the response body is the employee's name.
    }

    /**
     * Test case to verify that a delete operation fails with a 409 Conflict
     * when there are multiple employees with the same name.
     */
    @Test
    void testDeleteEmployeeWithDuplicateNameFails() throws JsonProcessingException {
        // Step 1: Creating the first employee.
        String sharedName = "Ambigous Employee Name";
        Map<String, Object> requestBody1 =
                Map.of("name", sharedName, "salary", 150000, "age", 40, "title", "Senior Tester");
        webTestClient
                .post()
                .uri("/")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(requestBody1), Map.class)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.OK);

        // Step 2: Creating a second employee with the same name.
        Map<String, Object> requestBody2 =
                Map.of("name", sharedName, "salary", 160000, "age", 45, "title", "Lead Developer");
        String responseBody = webTestClient
                .post()
                .uri("/")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(requestBody2), Map.class)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.OK)
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        // Parsing the JSON response to get the employee's ID.
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode createdEmployee = objectMapper.readTree(responseBody);
        String employeeId = createdEmployee.get("id").asText();

        // Step 3: Attempting to delete one of the employees using its ID and assert the 409 conflict response body.
        webTestClient
                .delete()
                .uri("/{id}", employeeId)
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.CONFLICT)
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.errorCode")
                .isEqualTo("employee_delete_failed")
                .jsonPath("$.errorMessage")
                .isEqualTo("Ambiguous deletion: multiple employees found with the same name");
    }
}
