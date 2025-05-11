package com.example.d_hub_user_service.service;

import com.example.d_hub_user_service.proxy.UserWithCompanyData;

import java.util.List;
import java.util.Optional;

public interface UserWithCompanyService {

    UserWithCompanyData getUserAndCompanyByUserId(Long id);

    Optional<List<UserWithCompanyData>> getUserWithCompanyDataByUserSurname(String surname);

    List<UserWithCompanyData> getAllUsers();

}
