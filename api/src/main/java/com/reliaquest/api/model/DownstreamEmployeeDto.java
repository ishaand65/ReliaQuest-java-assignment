package com.reliaquest.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
// @JsonIgnoreProperties - To whitelist additional properties in the EmployeeListWrapper class and prevent the Jackson
// parser from throwing an error
@JsonIgnoreProperties(ignoreUnknown = true)
public class DownstreamEmployeeDto {

    @JsonProperty("data")
    private List<Employee> employees;

    @JsonProperty("status")
    private String status;
}
