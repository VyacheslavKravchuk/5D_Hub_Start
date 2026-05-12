package com.example.d_hub_company_service.service;

import com.example.d_hub_company_service.entity.Company;

import java.util.List;
import java.util.Optional;

public interface CompanyService {

    boolean createCompany(Company company);

    Optional<Company> getCompanyById(Long id);

    Optional<Company> getCompanyByName(String name);

    void createEmployeeForCompany(Long employeeId, String nameCompany);

    List<Company> getAllCompany();
}
