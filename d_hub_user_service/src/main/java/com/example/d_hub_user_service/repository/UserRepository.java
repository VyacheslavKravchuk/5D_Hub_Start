package com.example.d_hub_user_service.repository;

import com.example.d_hub_user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<List<User>> findAllByLastName(String lastName);

    boolean existsByPhoneNumber(String name);

}
