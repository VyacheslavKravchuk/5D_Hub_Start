package com.example.d_hub_company_service.controller;


import com.example.d_hub_company_service.exception.CompanyNotFoundException;
import com.example.d_hub_company_service.proxy.EmployeesOfTheCompany;
import com.example.d_hub_company_service.service.UserWithCompanyService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Класс контроллер для получения полных данных извлеченных
 * из базы пользователей
 */

@RestController
@RequestMapping("/proxy/companies")
@RequiredArgsConstructor
public class UserWithCompanyController {

    private final UserWithCompanyService userWithCompanyService;

    private final Logger logger = LoggerFactory.getLogger(UserWithCompanyController.class);


    @Operation(summary = "Получение всех сотрудников компании по названию компании")
    @GetMapping("/{companyName}/employees")
    public ResponseEntity<EmployeesOfTheCompany> getAllUsersOfTheCompanyByCompanyName(
            @PathVariable String companyName) {
        logger.info("Получен запрос для получения сотрудников компании по названию: {}", companyName);
        try {
            EmployeesOfTheCompany employeesOfTheCompany = userWithCompanyService.getAllUsersOfTheCompanyByCompanyName(companyName);
            return ResponseEntity.ok(employeesOfTheCompany);
        } catch (CompanyNotFoundException e) {
            logger.error("Компания с названием {} не найдена.", companyName, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            logger.error("Ошибка при обработке запроса для компании {}: {}", companyName, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "Получение всех компаний и их сотрудников")
    @GetMapping("/all")
    public ResponseEntity<List<EmployeesOfTheCompany>> getAllCompaniesWithEmployees() {
        logger.info("Получен запрос для получения данных о всех компаниях и их сотрудниках.");
        Optional<List<EmployeesOfTheCompany>> companies = userWithCompanyService.getAllCompany();
        if (companies.isPresent()) {
            return ResponseEntity.ok(companies.get());
        } else {
            logger.warn("Компании с сотрудниками не найдены.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
    }
}