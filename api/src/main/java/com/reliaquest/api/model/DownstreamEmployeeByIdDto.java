package com.reliaquest.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
//

@Data
// @JsonIgnoreProperties - To whitelist additional properties in the EmployeeListWrapper class and prevent the Jackson
// parser from throwing an error
@JsonIgnoreProperties(ignoreUnknown = true)
public class DownstreamEmployeeByIdDto {

    @JsonProperty("data")
    private Employee employee;

    @JsonProperty("status")
    private String status;
}
