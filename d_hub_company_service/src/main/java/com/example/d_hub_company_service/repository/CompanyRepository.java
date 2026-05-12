package com.example.d_hub_company_service.repository;

import com.example.d_hub_company_service.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByName(String name);

    boolean existsByName(String name);
}
