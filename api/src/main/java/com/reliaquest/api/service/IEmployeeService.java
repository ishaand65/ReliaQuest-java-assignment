package com.reliaquest.api.service;

import com.reliaquest.api.model.CreateEmployeeInput;
import com.reliaquest.api.model.Employee;
import java.util.List;

public interface IEmployeeService {
    public List<Employee> getAllEmployees();

    public List<Employee> getEmployeesByNameSearch(String searchString);

    public Employee getEmployeeById(String id);

    public Integer getHighestSalaryOfEmployees();

    public List<String> getTopTenHighestEarningEmployeeNames();

    public Employee createEmployee(CreateEmployeeInput input);

    public String deleteEmployeeById(String id);
}
