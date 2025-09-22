package com.reliaquest.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateEmployeeInput {

    @JsonProperty("name")
    @NotBlank(message = "Name cannot be blank.")
    private String name;

    @JsonProperty("salary")
    @NotNull(message = "Salary cannot be null.") @Min(value = 1, message = "Salary must be greater than zero.")
    private Integer salary;

    @JsonProperty("age")
    @NotNull(message = "Age cannot be null.") @Min(value = 16, message = "Age must be between 16 and 75.")
    @Max(value = 75, message = "Age must be between 16 and 75.")
    private Integer age;

    @JsonProperty("title")
    @NotBlank(message = "Title cannot be blank.")
    private String title;
}
