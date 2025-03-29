package com.example.pizzaopgave.domain;

import com.example.pizzaopgave.domain.Pizza;
import com.example.pizzaopgave.domain.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private Long id;
    private User user;
    private List<Pizza> pizzas;
    private LocalDateTime orderDate;
    private double totalPrice;
    private boolean isCompleted;
    private int earnedBonusPoints;

    public Order(User user) {
        this.user = user;
        this.pizzas = new ArrayList<>();
        this.orderDate = LocalDateTime.now();
        this.isCompleted = false;
    }

    // For database retrieval
    public Order(Long id, User user, LocalDateTime orderDate, double totalPrice, boolean isCompleted, int earnedBonusPoints) {
        this.id = id;
        this.user = user;
        this.pizzas = new ArrayList<>();
        this.orderDate = orderDate;
        this.totalPrice = totalPrice;
        this.isCompleted = isCompleted;
        this.earnedBonusPoints = earnedBonusPoints;
    }

    public void addPizza(Pizza pizza) {
        pizzas.add(pizza);
        calculateTotalPrice();
    }

    public void removePizza(Pizza pizza) {
        pizzas.remove(pizza);
        calculateTotalPrice();
    }

    public void calculateTotalPrice() {
        totalPrice = 0;
        for (Pizza pizza : pizzas) {
            totalPrice += pizza.calculatePrice();
        }
    }

    public int calculateBonusPoints() {
        // For example, 1 point per 10 kr spent
        earnedBonusPoints = (int) (totalPrice / 10);
        return earnedBonusPoints;
    }

    public void completeOrder() {
        isCompleted = true;
        calculateBonusPoints();
        user.addBonusPoints(earnedBonusPoints);
        user.addOrder(this);
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Pizza> getPizzas() {
        return new ArrayList<>(pizzas);
    }

    public void setPizzas(List<Pizza> pizzas) {
        this.pizzas = new ArrayList<>(pizzas);
        calculateTotalPrice();
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public int getEarnedBonusPoints() {
        return earnedBonusPoints;
    }

    public void setEarnedBonusPoints(int earnedBonusPoints) {
        this.earnedBonusPoints = earnedBonusPoints;
    }
}