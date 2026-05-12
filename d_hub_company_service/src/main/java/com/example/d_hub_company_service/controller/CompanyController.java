package com.example.d_hub_company_service.controller;


import com.example.d_hub_company_service.entity.Company;
import com.example.d_hub_company_service.exception.CompanyNotFoundException;

import com.example.d_hub_company_service.exception.EmployeeAlreadyExistsException;
import com.example.d_hub_company_service.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


/**
 * Класс контроллер для регистрации компании и получения
 * данных из нее и идентификаторов пользователей
 * (для автономной работы)
 */

@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final Logger logger = LoggerFactory.getLogger(CompanyController.class);

    private final CompanyService companyService;

    @Operation(summary = "Регистрация компании")
    @PostMapping
    public ResponseEntity<?> createCompany (@RequestBody Company company) {
        logger.info("Запрос регистрации компании");
        companyService.createCompany(company);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Регистрация сотрудника в компании")
    @PostMapping("/{companyName}/employees")
    public ResponseEntity<?> createEmployeeForCompany(
            @PathVariable("companyName") String companyName,
            @RequestParam("employee_id") Long employeeId
    ) {
        logger.info("Запрос добавления сотрудника в компанию: {}", companyName);
        try {
            companyService.createEmployeeForCompany(employeeId, companyName);

            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (CompanyNotFoundException e) {
            logger.error("Не найдена компания: {}", companyName, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Компания отсутствует");
        } catch (EmployeeAlreadyExistsException e ) {
            logger.error("Такой идентификатор сотрудника в заданной компании уже существует: employeeId={}, companyName={}", employeeId, companyName, e);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @Operation(summary = "Получение информации об авторизованной компании по ID")
    @GetMapping("/by-id")
    public ResponseEntity<Optional<Company>> getCompanyById(@RequestParam("company_id") Long id) {
        logger.info("Request for getting user by id");

        try {
            Optional<Company> company = companyService.getCompanyById(id);
            return ResponseEntity.ok(company);
        } catch (CompanyNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Operation(summary = "Получение информации о всех авторизованных компаниях")
    @GetMapping("/all")
    public ResponseEntity<List<Company>> getAllCompany() {
        logger.info("Request for getting all companies");

        try {
            List<Company> companies = companyService.getAllCompany();
            return ResponseEntity.ok(companies);
        } catch (CompanyNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Operation(summary = "Получение информации об авторизованной компании по названию")
    @GetMapping("/by-name")
    public ResponseEntity<Optional<Company>> getCompanyByName(@RequestParam("name") String name) {
        logger.info("Request for getting company by name");

        try {
            Optional<Company> company = companyService.getCompanyByName(name);
            return ResponseEntity.ok(company);
        } catch (CompanyNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
