package com.example.d_hub_user_service.controller;

import com.example.d_hub_user_service.entity.User;
import com.example.d_hub_user_service.exception.UserNotFoundException;
import com.example.d_hub_user_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@Tag(name = "Авторизация и регистрация", description = "Authentication/Registration")
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final Logger logger =  LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @Operation(summary = "Регистрация пользователя"//,
    )
    @PostMapping
    public ResponseEntity<?>  createUser (@RequestBody User user) {
        logger.info("Запрос регистрации пользователя");
        userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Получение пользователя по фамилии")
    @GetMapping("/by-surname")
    public ResponseEntity<Optional<List<User>>> getUserBySurname(@RequestParam("last_name") String surname) {
        logger.info("Запрос получения пользователя по фамилии");

        try {
            Optional <List<User>> user = userService.getUserBySurname(surname);;
            return ResponseEntity.ok(user);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }

    @Operation(summary = "Получение информации об авторизованном пользователе по ID")
    @GetMapping("/by-id")
    public ResponseEntity<Optional<User>> getUserById(@RequestParam("user_id") Long id) {
        logger.info("Запрос получения пользователя по ID");
        try {
            Optional<User> user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


}
