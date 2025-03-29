package com.example.pizzaopgave.application.implementations;

import com.example.pizzaopgave.application.interfaces.IOrderService;
import com.example.pizzaopgave.domain.Order;
import com.example.pizzaopgave.domain.Pizza;
import com.example.pizzaopgave.domain.User;
import com.example.pizzaopgave.infrastructure.interfaces.IOrderRepository;
import com.example.pizzaopgave.infrastructure.interfaces.IPizzaRepository;
import com.example.pizzaopgave.infrastructure.interfaces.IUserRepository;

import java.util.List;
import java.util.Optional;

public class OrderService implements IOrderService {

    private final IOrderRepository orderRepository;
    private final IUserRepository userRepository;
    private final IPizzaRepository pizzaRepository;

    public OrderService(IOrderRepository orderRepository, IUserRepository userRepository, IPizzaRepository pizzaRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.pizzaRepository = pizzaRepository;
    }

    @Override
    public Order createOrder(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        Order order = new Order(userOptional.get());
        return orderRepository.save(order);
    }

    @Override
    public Order addPizzaToOrder(Long orderId, Long pizzaId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        Optional<Pizza> pizzaOptional = pizzaRepository.findById(pizzaId);

        if (orderOptional.isEmpty()) {
            throw new IllegalArgumentException("Order not found");
        }

        if (pizzaOptional.isEmpty()) {
            throw new IllegalArgumentException("Pizza not found");
        }

        Order order = orderOptional.get();

        if (order.isCompleted()) {
            throw new IllegalStateException("Cannot modify a completed order");
        }

        order.addPizza(pizzaOptional.get());

        return orderRepository.save(order);
    }

    @Override
    public Order removePizzaFromOrder(Long orderId, Long pizzaId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        Optional<Pizza> pizzaOptional = pizzaRepository.findById(pizzaId);

        if (orderOptional.isEmpty()) {
            throw new IllegalArgumentException("Order not found");
        }

        if (pizzaOptional.isEmpty()) {
            throw new IllegalArgumentException("Pizza not found");
        }

        Order order = orderOptional.get();

        if (order.isCompleted()) {
            throw new IllegalStateException("Cannot modify a completed order");
        }

        order.removePizza(pizzaOptional.get());

        return orderRepository.save(order);
    }

    @Override
    public Order completeOrder(Long orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);

        if (orderOptional.isEmpty()) {
            throw new IllegalArgumentException("Order not found");
        }

        Order order = orderOptional.get();

        if (order.isCompleted()) {
            throw new IllegalStateException("Order is already completed");
        }

        order.completeOrder();

        return orderRepository.save(order);
    }

    @Override
    public List<Order> getOrdersByUser(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        return orderRepository.findByUser(userOptional.get());
    }

    @Override
    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }
}