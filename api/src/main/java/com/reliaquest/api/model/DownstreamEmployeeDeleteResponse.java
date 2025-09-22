package com.reliaquest.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DownstreamEmployeeDeleteResponse {

    @JsonProperty("data")
    private Boolean data;

    @JsonProperty("status")
    private String status;
}
