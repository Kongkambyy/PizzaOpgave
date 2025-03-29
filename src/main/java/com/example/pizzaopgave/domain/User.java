package com.example.pizzaopgave.domain;

import org.springframework.core.annotation.Order;

import java.util.ArrayList;
import java.util.List;

public class User {
    private Long id;
    private String name;
    private String email;
    private String password;
    private String address;
    private int bonusPoints;
    private List<Order> orderHistory;

    public User(String name, String email, String password, String address) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.address = address;
        this.bonusPoints = 0;
        this.orderHistory = new ArrayList<>();
    }

    public User(Long id, String name, String email, String password, String address, int bonusPoints) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.address = address;
        this.bonusPoints = bonusPoints;
        this.orderHistory = new ArrayList<>();
    }

    public void addBonusPoints(int points) {
        this.bonusPoints += points;
    }

    public boolean useBonusPoints(int pointsToUse) {
        if (pointsToUse <= bonusPoints) {
            bonusPoints -= pointsToUse;
            return true;
        }
        return false;
    }

    public void addOrder(Order order) {
        this.orderHistory.add(order);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getBonusPoints() {
        return bonusPoints;
    }

    public void setBonusPoints(int bonusPoints) {
        this.bonusPoints = bonusPoints;
    }

    public List<Order> getOrderHistory() {
        return new ArrayList<>(orderHistory);
    }
}