package com.example.d_hub_user_service.controller;


import com.example.d_hub_user_service.exception.UserNotFoundException;
import com.example.d_hub_user_service.proxy.UserWithCompanyData;
import com.example.d_hub_user_service.service.UserWithCompanyService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

/**
 * Класс контроллер для получения извлеченных
 * данных из компании
 */

@Slf4j
@RestController
@RequestMapping("/users_companies")
@RequiredArgsConstructor
public class UserWithCompanyController {


    private final Logger logger =  LoggerFactory.getLogger(UserWithCompanyController.class);

    private final UserWithCompanyService userWithCompanyService;


    @Operation(summary = "Получение информации о всех пользователях" +
            "и компаний, в которых они числятся")
    @GetMapping
    public ResponseEntity<List<UserWithCompanyData>> getAllUsersWithCompanyData() {
        logger.info("Получен запрос на получение данных о всех пользователях с" +
                " информацией о компании.");

        try {
            List<UserWithCompanyData> usersWithCompanyData = userWithCompanyService.getAllUsers();

            if (usersWithCompanyData.isEmpty()) {
                logger.info("Не найдено ни одного пользователя с информацией о компании.");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            logger.info("Успешно получены данные о {} пользователях с информацией о компании.", usersWithCompanyData.size());
            return new ResponseEntity<>(usersWithCompanyData, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Произошла ошибка при получении данных о пользователях с информацией о компании: ", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Получение информации об авторизованном пользователе" +
            "и компании, в которой он числится по ID")
    @GetMapping("/{id}")
    public ResponseEntity<UserWithCompanyData> getUserById(@PathVariable Long id) {
        try {
            UserWithCompanyData userWithCompanyData = userWithCompanyService.getUserAndCompanyByUserId(id);
            return ResponseEntity.ok(userWithCompanyData);
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @Operation(summary = "Получение информации об авторизованном пользователе" +
            "и компании, в которой он числится по фамилии")
    @GetMapping("/by-surname")
    public ResponseEntity<List<UserWithCompanyData>> getUsersBySurname(@RequestParam String surname) {
        try {
            Optional<List<UserWithCompanyData>> users =
                    userWithCompanyService.getUserWithCompanyDataByUserSurname(surname);
            return users.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        } catch (UserNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }
}
