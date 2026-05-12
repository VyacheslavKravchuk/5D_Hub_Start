package com.example.d_hub_company_service.service.impl;

import com.example.d_hub_company_service.entity.Company;
import com.example.d_hub_company_service.exception.CompanyNotFoundException;
import com.example.d_hub_company_service.exception.UserNotFoundException;
import com.example.d_hub_company_service.exception.UserServiceException;
import com.example.d_hub_company_service.proxy.EmployeesOfTheCompany;
import com.example.d_hub_company_service.proxy.UserData;
import com.example.d_hub_company_service.repository.CompanyRepository;
import com.example.d_hub_company_service.service.UserWithCompanyService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserWithCompanyServiceImpl implements UserWithCompanyService {


    private final Logger logger = LoggerFactory.getLogger(UserWithCompanyServiceImpl.class);

    private final CompanyRepository companyRepository;

    private final String companyServiceUrl = "http://localhost:8081/users/by-id?user_id=";

    @Autowired
    private RestTemplate restTemplate;


    public EmployeesOfTheCompany getAllUsersOfTheCompanyByCompanyName(String companyName) {
        logger.info("Вызван метод для получения данных о компании и всех ее сотрудников " +
                "по названию компании: {}", companyName);

        Company company = companyRepository.findByName(companyName)
                .orElseThrow(() -> new CompanyNotFoundException("Компания с названием "
                        + companyName + " не найдена"));

        List<Long> employeeIds = company.getEmployeeIds();

        Map<Long, UserData> userDataMap = employeeIds.stream()
                .collect(Collectors.toMap(
                        userId -> userId,
                        this::getUserFromUserService
                ));

        EmployeesOfTheCompany employeesOfTheCompany = new EmployeesOfTheCompany();
        employeesOfTheCompany.setCompany(company);
        employeesOfTheCompany.setEmployees(userDataMap);

        return employeesOfTheCompany;
    }

    public Optional<List<EmployeesOfTheCompany>> getAllCompany() {
        logger.info("Вызван метод для получения данных о всех компаниях и их сотрудниках.");

        List<Company> companies = companyRepository.findAll();
        List<EmployeesOfTheCompany> employeesOfTheCompanies = new ArrayList<>();

        if (companies.isEmpty()) {
            logger.warn("Компании не найдены.");
            return Optional.empty();
        }

        for (Company company : companies) {
            try {
                EmployeesOfTheCompany employeesOfTheCompany
                        = getAllUsersOfTheCompanyByCompanyName(company.getName());
                employeesOfTheCompanies.add(employeesOfTheCompany);
            } catch (CompanyNotFoundException e) {
                logger.error("Компания с названием {} не найдена.", company.getName(), e);
            } catch (UserNotFoundException e) {
                logger.error("Ошибка при получении пользователей компании {}.", company.getName(), e);
            } catch (Exception e) {
                logger.error("Непредвиденная ошибка при обработке компании {}.", company.getName(), e);
            }
        }

        if (employeesOfTheCompanies.isEmpty()) {
            logger.warn("Не удалось обработать ни одну компанию.");
            return Optional.empty();
        }

        return Optional.of(employeesOfTheCompanies);
    }

    public UserData getUserFromUserService(Long userId) {
        logger.info("Запрос данных пользователя с ID: {} из сервиса пользователей", userId);

        try {
            ResponseEntity<UserData> response = restTemplate.getForEntity(companyServiceUrl
                    + userId, UserData.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                logger.debug("Данные пользователя с ID {} успешно получены", userId);
                return response.getBody();
            } else {
                // Обработка неожиданных кодов ответа (например, 204 No Content)
                logger.warn("Получен неожиданный код ответа: {} при запросе пользователя с ID {}",
                        response.getStatusCode(), userId);
                throw new UserNotFoundException("Не удалось получить данные пользователя с ID "
                        + userId + ". Код ответа: " + response.getStatusCode());
            }

        } catch (HttpClientErrorException e) {
            logger.error("Клиентская ошибка при запросе пользователя с ID {}: {}", userId, e.getMessage());

            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new UserNotFoundException("Пользователь с ID " + userId +
                        " не найден в сервисе пользователей.");
            } else {
                throw new UserServiceException("Ошибка при запросе пользователя с ID "
                        + userId + ": " + e.getMessage());
            }
        } catch (HttpServerErrorException e) {
            logger.error("Серверная ошибка при запросе пользователя с ID {}: {}", userId, e.getMessage());
            throw new UserServiceException("Серверная ошибка при запросе пользователя с ID "
                    + userId + ": " + e.getMessage());
        } catch (ResourceAccessException e) {
            logger.error("Сервис пользователей недоступен при запросе пользователя с ID {}: {}",
                    userId, e.getMessage());
            throw new UserServiceException("Сервис пользователей недоступен.");
        } catch (Exception e) {
            logger.error("Непредвиденная ошибка при запросе пользователя с ID {}: {}", userId, e.getMessage());
            throw new UserServiceException("Непредвиденная ошибка при запросе пользователя с ID " + userId +
                    ": " + e.getMessage());
        }
    }
}
