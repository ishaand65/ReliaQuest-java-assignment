package com.reliaquest.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.dto.Employee;
import com.reliaquest.api.dto.DownstreamEmployeeByIdDto;
import com.reliaquest.api.dto.DownstreamEmployeeDto;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.URI;
import java.net.http.HttpResponse;
import java.util.List;

@Component
public class MockServerClient {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    private static final String API_URL = "http://localhost:8112";

    MockServerClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public List<Employee> getAllEmployee() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/api/v1/employee"))
                .GET() // Making a GET request
                .build();

        // Send the request and get the response as a string
        HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // TODO: Print the status code and the response body received from the server - use logger
        System.out.println("Status Code: " + response.statusCode());
        System.out.println("Response Body: " + response.body());

        // Deserialize the JSON array into a List of Employee objects using TypeReference
        DownstreamEmployeeDto employeesResponse = this.objectMapper.readValue(response.body(), DownstreamEmployeeDto.class);

        return employeesResponse.getEmployees();
    }

    public Employee getEmployeeById(String id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/api/v1/employee/" + id))
                .GET() // Making a GET request
                .build();

        // Send the request and get the response as a string
        HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // TODO: Print the status code and the response body received from the server - use logger
        System.out.println("Status Code: " + response.statusCode());
        System.out.println("Response Body: " + response.body());

        // Deserialize the JSON array into a List of Employee objects using TypeReference
        DownstreamEmployeeByIdDto employeeResponse = this.objectMapper.readValue(response.body(), DownstreamEmployeeByIdDto.class);

        return employeeResponse.getEmployee();
    }
}
