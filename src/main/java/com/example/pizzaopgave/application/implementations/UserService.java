package com.example.pizzaopgave.application.implementations;

import com.example.pizzaopgave.application.interfaces.IUserService;
import com.example.pizzaopgave.domain.Order;
import com.example.pizzaopgave.domain.User;
import com.example.pizzaopgave.infrastructure.interfaces.IOrderRepository;
import com.example.pizzaopgave.infrastructure.interfaces.IUserRepository;

import java.util.List;
import java.util.Optional;

public class UserService implements IUserService {

    private final IUserRepository userRepository;
    private final IOrderRepository orderRepository;

    public UserService(IUserRepository userRepository, IOrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public User createUser(User user) {
        // Check if user with the same email already exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        return userRepository.save(user);
    }

    @Override
    public Optional<User> login(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getPassword().equals(password)) {
                return Optional.of(user);
            }
        }

        return Optional.empty();
    }

    @Override
    public List<Order> getUserOrderHistory(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        return orderRepository.findByUser(userOptional.get());
    }

    @Override
    public User addBonusPoints(Long userId, int points) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        User user = userOptional.get();
        user.addBonusPoints(points);

        return userRepository.save(user);
    }

    @Override
    public boolean useBonusPoints(Long userId, int points) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        User user = userOptional.get();
        boolean success = user.useBonusPoints(points);

        if (success) {
            userRepository.save(user);
        }

        return success;
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }
}