package com.example.pizzaopgave.application.interfaces;

import com.example.pizzaopgave.domain.User;
import org.springframework.core.annotation.Order;

import java.util.List;
import java.util.Optional;

public interface IUserService {

    User createUser(User user);

    Optional<User> login(String email, String password);

    List<Order> getUserOrderHistory(Long userId);

    User addBonusPoints(Long userId, int points);

    boolean useBonusPoints(Long userId, int points);

    Optional<User> getUserById(Long userId);
}