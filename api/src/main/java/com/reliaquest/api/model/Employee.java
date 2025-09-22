package com.reliaquest.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * A POJO (Plain Old Java Object) representing an Employee.
 * It is designed to be easily serialized to and deserialized from a JSON object.
 */
@Data
@NoArgsConstructor
@ToString
@AllArgsConstructor
// @JsonIgnoreProperties - To whitelist additional properties in the Employee class and prevent the Jackson parser from
// throwing an error
@JsonIgnoreProperties(ignoreUnknown = true)
public class Employee {

    @JsonProperty("id")
    private String id;

    @JsonProperty("employee_name")
    private String employeeName;

    @JsonProperty("employee_salary")
    private Integer employeeSalary;

    @JsonProperty("employee_age")
    private Integer employeeAge;

    @JsonProperty("employee_title")
    private String employeeTitle;

    @JsonProperty("employee_email")
    private String employeeEmail;
}
