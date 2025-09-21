package com.reliaquest.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * A POJO (Plain Old Java Object) representing an Employee.
 * It is designed to be easily serialized to and deserialized from a JSON object.
 */
@Getter
@Setter
// @JsonIgnoreProperties - To whitelist additional properties in the Employee class and prevent the Jackson parser from throwing an error
@JsonIgnoreProperties(ignoreUnknown = true)
public class Employee {

    private String id;

    @JsonProperty("employee_name")
    private String employeeName;

    @JsonProperty("employee_salary")
    private int employeeSalary;

    @JsonProperty("employee_age")
    private int employeeAge;

    @JsonProperty("employee_title")
    private String employeeTitle;

    @JsonProperty("employee_email")
    private String employeeEmail;

    // Default no-argument constructor is required for some frameworks like Jackson
    public Employee() {
    }

    /**
     * Constructor for creating an Employee object with all fields.
     * @param id The employee's unique ID.
     * @param employeeName The employee's name.
     * @param employeeSalary The employee's salary.
     * @param employeeAge The employee's age.
     * @param employeeTitle The employee's job title.
     * @param employeeEmail The employee's email.
     */
    public Employee(String id, String employeeName, int employeeSalary, int employeeAge, String employeeTitle, String employeeEmail) {
        this.id = id;
        this.employeeName = employeeName;
        this.employeeSalary = employeeSalary;
        this.employeeAge = employeeAge;
        this.employeeTitle = employeeTitle;
        this.employeeEmail = employeeEmail;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id='" + id + '\'' +
                ", employeeName='" + employeeName + '\'' +
                ", employeeSalary=" + employeeSalary +
                ", employeeAge=" + employeeAge +
                ", employeeTitle='" + employeeTitle + '\'' +
                ", employeeEmail='" + employeeEmail + '\'' +
                '}';
    }
}
