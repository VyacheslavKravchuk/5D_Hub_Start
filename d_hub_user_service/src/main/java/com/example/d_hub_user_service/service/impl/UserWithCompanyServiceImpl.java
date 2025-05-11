package com.example.d_hub_user_service.service.impl;

import com.example.d_hub_user_service.entity.User;
import com.example.d_hub_user_service.exception.CompanyNotFoundException;
import com.example.d_hub_user_service.exception.CompanyServiceException;
import com.example.d_hub_user_service.exception.UserNotFoundException;
import com.example.d_hub_user_service.proxy.CompanyData;
import com.example.d_hub_user_service.proxy.UserWithCompanyData;
import com.example.d_hub_user_service.repository.UserRepository;
import com.example.d_hub_user_service.service.UserWithCompanyService;
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

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserWithCompanyServiceImpl implements UserWithCompanyService {


    private final Logger logger = LoggerFactory.getLogger(UserWithCompanyServiceImpl.class);

    private final UserRepository userRepository;

    private final String companyServiceUrl = "http://localhost:8082/companies/by-id?company_id=";

    private final String companyGetAllServiceUrl = "http://localhost:8082/companies/all";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public List<UserWithCompanyData> getAllUsers() {
        logger.info("Вызван метод для получения данных о всех компаниях и их сотрудниках.");

        List<User> users = userRepository.findAll();

        if (users.isEmpty()) {
            logger.warn("Сотрудники не найдены.");
            return Collections.emptyList();
        }

        List<UserWithCompanyData> userWithCompanyDataList = new ArrayList<>();
        //List<Long> companyIds = users.stream().map(User::getCompanyId).toList();

        try {
            Map<Long, CompanyData> companyDataMap = getAllCompaniesFromCompanyService().stream()
                    .collect(Collectors.toMap(CompanyData::getId, Function.identity()));

            for (User user : users) {
                Long companyId = user.getCompanyId();
                CompanyData companyData = companyDataMap.get(companyId);

                if (companyData != null) {
                    userWithCompanyDataList.add(new UserWithCompanyData(user, companyData));
                } else {
                    logger.warn("Не найдены данные о компании с id {} для пользователя с id {}",
                            companyId, user.getUserId());
                }
            }


        } catch (Exception e) {
            logger.error("Непредвиденная ошибка при получении данных о компаниях: ", e);
            return Collections.emptyList();
        }

        return userWithCompanyDataList;
    }


    @Override
    public UserWithCompanyData getUserAndCompanyByUserId(Long id) {
        logger.info("Был вызван метод для получения пользователей с данными о компании по id");

        return userRepository.findById(id)
                .map(user -> {
                    Long companyId = user.getCompanyId();
                    CompanyData companyData;
                    try {
                        companyData = getCompanyFromCompanyService(companyId);
                    } catch (CompanyNotFoundException e) {

                        logger.warn("Компания с id {} не найдена.", companyId);
                        companyData = null;
                    }
                    return new UserWithCompanyData(user, companyData);
                })
                .orElseThrow(() -> new UserNotFoundException("Пользователь отсутствует"));
    }


    @Override
    public Optional<List<UserWithCompanyData>> getUserWithCompanyDataByUserSurname(String surname) {
        logger.info("Был вызван метод для получения пользователей с данными о компании по фамилии");

        Optional<List<User>> usersOptional = userRepository.findAllByLastName(surname);

        if (usersOptional.isPresent()) {
            List<User> users = usersOptional.get();

            List<UserWithCompanyData> userWithCompanyDataList = users.stream()
                    .map(user -> {
                        CompanyData companyData = getCompanyFromCompanyService(user.getCompanyId());
                        if (companyData != null) {
                            return new UserWithCompanyData(user, companyData);
                        } else {
                            logger.warn("Не найдена компания для пользователя с ID: {}", user.getUserId());
                            return new UserWithCompanyData(user, null);
                        }
                    })
                    .filter(userWithCompanyData -> userWithCompanyData.getCompanyData() != null)
                    .collect(Collectors.toList());

            if (userWithCompanyDataList.isEmpty()) {
                logger.warn("Для пользователей с фамилией {} не найдено данных о компаниях.", surname);
                return Optional.empty();
            }
            return Optional.of(userWithCompanyDataList);

        } else {
            throw new UserNotFoundException("Пользователь с фамилией " + surname + " не найден");
        }
    }


    public CompanyData getCompanyFromCompanyService(Long companyId) {

        try {
            ResponseEntity<CompanyData> response = restTemplate.getForEntity(companyServiceUrl
                    + companyId, CompanyData.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                logger.debug("Данные компании с ID {} успешно получены", companyId);
                return response.getBody();
            } else {
                logger.warn("Получен неожиданный код ответа: {} при запросе компании с ID {}",
                        response.getStatusCode(), companyId);
                throw new CompanyServiceException("Не удалось получить данные компании с ID "
                        + companyId + ". Код ответа: " + response.getStatusCode());
            }

        } catch (HttpClientErrorException e) {
            logger.error("Клиентская ошибка при запросе компании с ID {}: {}", companyId, e.getMessage());

            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new CompanyNotFoundException("Компания с ID " + companyId +
                        " не найдена в сервисе компаний.");
            } else {
                throw new CompanyServiceException("Ошибка при запросе компании с ID "
                        + companyId + ": " + e.getMessage());
            }
        } catch (HttpServerErrorException e) {
            logger.error("Серверная ошибка при запросе компании с ID {}: {}", companyId, e.getMessage());
            throw new CompanyServiceException("Серверная ошибка при запросе компании с ID "
                    + companyId + ": " + e.getMessage());
        } catch (ResourceAccessException e) {
            logger.error("Сервис компаний недоступен при запросе компании с ID {}: {}",
                    companyId, e.getMessage());
            throw new CompanyServiceException("Сервис компаний недоступен.");
        } catch (Exception e) {
            logger.error("Непредвиденная ошибка при запросе компании с ID {}: {}", companyId, e.getMessage());
            throw new CompanyServiceException("Непредвиденная ошибка при запросе компании с ID " + companyId +
                    ": " + e.getMessage());
        }
    }

    public List<CompanyData> getAllCompaniesFromCompanyService() {

        try {
            ResponseEntity<CompanyData[]> response = restTemplate.getForEntity(companyGetAllServiceUrl,
                    CompanyData[].class);

            if (response.getStatusCode() == HttpStatus.OK) {
                logger.debug("Данные всех компаний успешно получены");
                return List.of(response.getBody());
            } else {
                logger.warn("Получен неожиданный код ответа: {} при запросе всех компаний",
                        response.getStatusCode());
                throw new CompanyServiceException("Не удалось получить данные о всех компаниях. Код ответа: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            logger.error("Клиентская ошибка при запросе всех компаний: {}", e.getMessage());
            throw new CompanyServiceException("Ошибка при запросе всех компаний: " + e.getMessage());
        } catch (HttpServerErrorException e) {
            logger.error("Серверная ошибка при запросе всех компаний: {}", e.getMessage());
            throw new CompanyServiceException("Серверная ошибка при запросе всех компаний: " + e.getMessage());
        } catch (ResourceAccessException e) {
            logger.error("Сервис компаний недоступен при запросе всех компаний: {}", e.getMessage());
            throw new CompanyServiceException("Сервис компаний недоступен.");
        } catch (Exception e) {
            logger.error("Непредвиденная ошибка при запросе всех компаний: {}", e.getMessage());
            throw new CompanyServiceException("Непредвиденная ошибка при запросе всех компаний: " + e.getMessage());
        }
    }

}
