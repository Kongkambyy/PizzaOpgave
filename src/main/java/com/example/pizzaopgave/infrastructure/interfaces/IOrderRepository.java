package com.example.pizzaopgave.infrastructure.interfaces;

import com.example.pizzaopgave.domain.Order;
import com.example.pizzaopgave.domain.User;

import java.util.List;
import java.util.Optional;

public interface IOrderRepository {

    Order save(Order order);

    Optional<Order> findById(Long id);

    List<Order> findByUser(User user);

    List<Order> findAll();

    void deleteById(Long id);
}