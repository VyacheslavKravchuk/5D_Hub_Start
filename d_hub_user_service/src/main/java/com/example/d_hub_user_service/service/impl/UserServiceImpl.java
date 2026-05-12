package com.example.d_hub_user_service.service.impl;

import com.example.d_hub_user_service.entity.User;
import com.example.d_hub_user_service.exception.UserAlreadyExistsException;
import com.example.d_hub_user_service.exception.UserNotFoundException;

import com.example.d_hub_user_service.repository.UserRepository;
import com.example.d_hub_user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;


    @Override
    public boolean createUser(User user) {
        logger.info("Запрос регистрации пользователя");

        if (userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            logger.warn("Пользователь с номером телефона {} уже существует", user.getPhoneNumber());
            throw new UserAlreadyExistsException("Пользователь с таким номером телефона '"
                    + user.getPhoneNumber() + "' уже имеется");
        }

        userRepository.save(user);
        logger.info("User registered with phone number: {}", user.getPhoneNumber());
        return true;
    }


    @Override
    public Optional<User> getUserById(Long id) {
        logger.info("Запрос получения пользователя по ID");
        if (userRepository.findById(id).isPresent()) {
            Optional<User> user = userRepository.findById(id);

            return user;
        } else {
            throw new UserNotFoundException("Пользователь c заданным ID отсутствует");
        }
    }

    @Override
    public Optional<List<User>> getUserBySurname(String surname) {
        logger.info("Was invoked method for getting user by surname");

        if (userRepository.findAllByLastName(surname).isPresent()) {
            Optional<List<User>> users = userRepository.findAllByLastName(surname);

            return users;
        } else {
            throw new UserNotFoundException("Пользователь отсутствует");
        }
    }

}
