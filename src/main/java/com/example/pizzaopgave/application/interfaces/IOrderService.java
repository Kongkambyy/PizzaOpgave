package com.example.pizzaopgave.application.interfaces;

import org.springframework.core.annotation.Order;

import java.util.List;
import java.util.Optional;

public interface IOrderService {

    Order createOrder(Long userId);

    Order addPizzaToOrder(Long orderId, Long pizzaId);

    Order removePizzaFromOrder(Long orderId, Long pizzaId);

    Order completeOrder(Long orderId);

    List<Order> getOrdersByUser(Long userId);

    Optional<Order> getOrderById(Long orderId);
}