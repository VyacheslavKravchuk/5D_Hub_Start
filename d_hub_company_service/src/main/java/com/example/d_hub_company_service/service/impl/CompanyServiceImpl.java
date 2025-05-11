package com.example.d_hub_company_service.service.impl;

import com.example.d_hub_company_service.entity.Company;
import com.example.d_hub_company_service.exception.CompanyAlreadyExistsException;
import com.example.d_hub_company_service.exception.CompanyNotFoundException;
import com.example.d_hub_company_service.exception.EmployeeAlreadyExistsException;
import com.example.d_hub_company_service.repository.CompanyRepository;
import com.example.d_hub_company_service.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    private final Logger logger = LoggerFactory.getLogger(CompanyServiceImpl.class);


    @Override
    public boolean createCompany(Company company) {
        logger.info("Был вызван метод регистрации компании");

        if (companyRepository.existsByName(company.getName())) {
            logger.warn("Компания с данным названием {} уже существует", company.getName());
            throw new CompanyAlreadyExistsException("Компания с названием '" + company.getName() + "' уже существует");
        }

        companyRepository.save(company);
        logger.info("Зарегистрирована компания с названием: {}", company.getName());
        return true;
    }


    @Override
    public Optional<Company> getCompanyById(Long id) {
        logger.info("Был вызван метод получения компании по ID");

        if (companyRepository.findById(id).isPresent()) {
            Optional<Company> company = companyRepository.findById(id);
            return company;
        } else {
            throw new CompanyNotFoundException("Компания отсутствует");
        }
    }

    @Override
    public Optional<Company> getCompanyByName(String name) {
        logger.info("Был вызван метод получения компании по ее названию");

        if (companyRepository.findByName(name).isPresent()) {
            Optional<Company> company = companyRepository.findByName(name);
            return company;
        } else {
            throw new CompanyNotFoundException("Компания отсутствует");
        }
    }


    @Transactional
    @Override
    public void createEmployeeForCompany(Long employeeId, String companyName) {
        logger.info("Добавление сотрудника с идентификатором {} в компанию {}", employeeId, companyName);

        Optional<Company> companyOptional = companyRepository.findByName(companyName);
        if (companyOptional.isEmpty()) {
            throw new CompanyNotFoundException("Не найдена компания: " + companyName);
        }
        Company company = companyOptional.get();

        List<Long> employeeIds = company.getEmployeeIds();
        if (employeeIds == null) {
            employeeIds = new ArrayList<>();
        }

        if (!employeeIds.contains(employeeId)) {
            employeeIds.add(employeeId);
            company.setEmployeeIds(employeeIds);
            companyRepository.save(company);
            logger.info("Сотрудник с идентификатором {} добавлен в компанию {}", employeeId, companyName);
        } else {
            logger.warn("Сотрудник с идентификатором {} уже имеется в компании {}", employeeId, companyName);
            throw new EmployeeAlreadyExistsException("Сотрудник уже существует");
        }
    }

    @Override
    public List<Company> getAllCompany() {
        logger.info("Вызван метод для получения данных о всех компаниях.");
        try {
            List<Company> companies = companyRepository.findAll();

            if (companies.isEmpty()) {
                logger.warn("Компании не найдены.");
                return Collections.emptyList();
            }

            return companies;

        } catch (Exception e) {
            logger.error("Ошибка при получении данных о компаниях: ", e);
            return Collections.emptyList();
        }
    }

}
