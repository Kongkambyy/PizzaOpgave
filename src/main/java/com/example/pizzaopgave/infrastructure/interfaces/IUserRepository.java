package com.example.pizzaopgave.infrastructure.interfaces;

import com.example.pizzaopgave.domain.User;

import java.util.List;
import java.util.Optional;

public interface IUserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    List<User> findAll();

    void deleteById(Long id);
}