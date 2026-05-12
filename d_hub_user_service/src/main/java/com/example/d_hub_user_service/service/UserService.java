package com.example.d_hub_user_service.service;


import com.example.d_hub_user_service.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    boolean createUser(User user);

    Optional<User> getUserById(Long id);

    Optional<List<User>> getUserBySurname(String surname);



}
