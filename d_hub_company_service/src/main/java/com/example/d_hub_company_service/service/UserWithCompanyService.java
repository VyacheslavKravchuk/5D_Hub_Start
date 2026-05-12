package com.example.d_hub_company_service.service;

import com.example.d_hub_company_service.proxy.EmployeesOfTheCompany;

import java.util.List;
import java.util.Optional;

public interface UserWithCompanyService {
    EmployeesOfTheCompany getAllUsersOfTheCompanyByCompanyName(String companyName);

    Optional<List<EmployeesOfTheCompany>> getAllCompany();

}
