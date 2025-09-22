package com.reliaquest.api.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.exception.ApiException;
import com.reliaquest.api.model.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MockServerClient {

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_JSON = "application/json";
    public static final String DELETE = "DELETE";
    public static final String API_INPUT_SERIALIZATION_FAILURE = "API Input serialization failure";
    public static final String DOWNSTREAM_API_FAILURE = "Downstream API failure";
    public static final String DOWNSTREAM_API_RESPONSE_PROCESSING_FAILURE =
            "Downstream API response processing failure";
    public static final String EMPLOYEE_NOT_FOUND = "employee_not_found";
    public static final String BASE_PATH = "/api/v1/employee";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private static final String API_URL = "http://localhost:8112";

    public MockServerClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public List<Employee> getAllEmployees() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + BASE_PATH))
                .GET() // Making a GET request
                .build();

//        // Send the request and get the response as a string
//        HttpResponse<String> response = null;
//        try {
//            response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
//        } catch (IOException | InterruptedException e) {
//            throw new ApiException(
//                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
//                    DOWNSTREAM_API_FAILURE,
//                    HttpStatus.INTERNAL_SERVER_ERROR.value());
//        }
//
//        // TODO: Print the status code and the response body received from the server - use logger
//        log.info("Status Code: " + response.statusCode());
//        log.info("Response Body: " + response.body());
//
//        if (HttpStatus.valueOf(response.statusCode()).isError())
//            throw new ApiException(
//                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
//                    DOWNSTREAM_API_FAILURE,
//                    HttpStatus.INTERNAL_SERVER_ERROR.value());
//
//        DownstreamEmployeeDto employeesResponse = null;
//        try {
//            employeesResponse = this.objectMapper.readValue(response.body(), DownstreamEmployeeDto.class);
//        } catch (JsonProcessingException e) {
//            throw new ApiException(
//                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
//                    DOWNSTREAM_API_RESPONSE_PROCESSING_FAILURE,
//                    HttpStatus.INTERNAL_SERVER_ERROR.value());
//        }

        DownstreamEmployeeDto employeesResponse = this.invokeApi(request, DownstreamEmployeeDto.class);

        return employeesResponse.getEmployees();
    }

    public Employee getEmployeeById(String id) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/api/v1/employee/" + id))
                .GET()
                .build();

        HttpResponse<String> response = null;
        try {
            response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new ApiException(
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    DOWNSTREAM_API_FAILURE,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        log.info("Status Code: " + response.statusCode());
        log.info("Response Body: " + response.body());

        if (response.statusCode() == HttpStatus.NOT_FOUND.value())
            throw new ApiException(
                    EMPLOYEE_NOT_FOUND,
                    "Employee information not found for Id: [%s]".formatted(id),
                    HttpStatus.NOT_FOUND.value());

        if (HttpStatus.valueOf(response.statusCode()).isError())
            throw new ApiException(
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    DOWNSTREAM_API_FAILURE,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());

        DownstreamEmployeeByIdDto employeeResponse = null;
        try {
            employeeResponse = this.objectMapper.readValue(response.body(), DownstreamEmployeeByIdDto.class);
        } catch (JsonProcessingException e) {
            throw new ApiException(
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    DOWNSTREAM_API_RESPONSE_PROCESSING_FAILURE,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return employeeResponse.getEmployee();
    }

    public Employee createEmployee(CreateEmployeeInput input) {

        String serializedInput = null;
        try {
            serializedInput = this.objectMapper.writeValueAsString(input);
        } catch (JsonProcessingException e) {
            throw new ApiException(
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    API_INPUT_SERIALIZATION_FAILURE,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + BASE_PATH))
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .POST(HttpRequest.BodyPublishers.ofString(serializedInput))
                .build();

        DownstreamEmployeeByIdDto employeeResponse = this.invokeApi(request, DownstreamEmployeeByIdDto.class);

//        // Send the request and get the response as a string
//        HttpResponse<String> response = null;
//        try {
//            response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
//        } catch (IOException | InterruptedException e) {
//            throw new ApiException(
//                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
//                    DOWNSTREAM_API_FAILURE,
//                    HttpStatus.INTERNAL_SERVER_ERROR.value());
//        }
//
//        log.info("Status Code: " + response.statusCode());
//        log.info("Response Body: " + response.body());
//
//        if (HttpStatus.valueOf(response.statusCode()).isError())
//            throw new ApiException(
//                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
//                    DOWNSTREAM_API_FAILURE,
//                    HttpStatus.INTERNAL_SERVER_ERROR.value());
//
//        DownstreamEmployeeByIdDto employeeResponse = null;
//        try {
//            employeeResponse = this.objectMapper.readValue(response.body(), DownstreamEmployeeByIdDto.class);
//        } catch (JsonProcessingException e) {
//            throw new ApiException(
//                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
//                    DOWNSTREAM_API_RESPONSE_PROCESSING_FAILURE,
//                    HttpStatus.INTERNAL_SERVER_ERROR.value());
//        }

        return employeeResponse.getEmployee();
    }

    public DownstreamEmployeeDeleteResponse deleteEmployee(DeleteEmployeeInput deleteEmployeeInput) {

        // Convert the POJO to a JSON string
        String jsonBody = null;
        try {
            jsonBody = this.objectMapper.writeValueAsString(deleteEmployeeInput);
        } catch (JsonProcessingException e) {
            throw new ApiException(
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    API_INPUT_SERIALIZATION_FAILURE,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + BASE_PATH))
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .method(DELETE, HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        return this.invokeApi(request, DownstreamEmployeeDeleteResponse.class);

//        HttpResponse<String> response = null;
//        try {
//            response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
//        } catch (IOException | InterruptedException e) {
//            throw new ApiException(
//                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
//                    DOWNSTREAM_API_FAILURE,
//                    HttpStatus.INTERNAL_SERVER_ERROR.value());
//        }
//
//        log.info("Status Code: " + response.statusCode());
//        log.info("Response Body: " + response.body());
//
//        if (HttpStatus.valueOf(response.statusCode()).isError())
//            throw new ApiException(
//                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
//                    DOWNSTREAM_API_FAILURE,
//                    HttpStatus.INTERNAL_SERVER_ERROR.value());
//
//        try {
//            return this.objectMapper.readValue(response.body(), DownstreamEmployeeDeleteResponse.class);
//        } catch (JsonProcessingException e) {
//            throw new ApiException(
//                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
//                    DOWNSTREAM_API_RESPONSE_PROCESSING_FAILURE,
//                    HttpStatus.INTERNAL_SERVER_ERROR.value());
//        }
    }

    private <T> T invokeApi(HttpRequest request, Class<T> responseClass) {
        HttpResponse<String> response = null;
        try {
            response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new ApiException(
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    DOWNSTREAM_API_FAILURE,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        log.info("Status Code: " + response.statusCode());
        log.info("Response Body: " + response.body());

        if (HttpStatus.valueOf(response.statusCode()).isError())
            throw new ApiException(
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    DOWNSTREAM_API_FAILURE,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());

        try {
            return this.objectMapper.readValue(response.body(), responseClass);
        } catch (JsonProcessingException e) {
            throw new ApiException(
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    DOWNSTREAM_API_RESPONSE_PROCESSING_FAILURE,
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

}
