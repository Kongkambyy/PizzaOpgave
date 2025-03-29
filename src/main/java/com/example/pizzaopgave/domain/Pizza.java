package com.example.pizzaopgave.domain;

import java.util.ArrayList;
import java.util.List;

public class Pizza {
    private Long id;
    private String name;
    private String description;
    private double basePrice;
    private List<Topping> toppings;

    public Pizza(String name, String description, double basePrice) {
        this.name = name;
        this.description = description;
        this.basePrice = basePrice;
        this.toppings = new ArrayList<>();
    }

    public Pizza(Long id, String name, String description, double basePrice) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.basePrice = basePrice;
        this.toppings = new ArrayList<>();
    }

    public void addTopping(Topping topping) {
        toppings.add(topping);
    }

    public void removeTopping(Topping topping) {
        toppings.remove(topping);
    }

    public double calculatePrice() {
        double totalPrice = basePrice;
        for (Topping topping : toppings) {
            totalPrice += topping.getPrice();
        }
        return totalPrice;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public List<Topping> getToppings() {
        return new ArrayList<>(toppings);
    }

    public void setToppings(List<Topping> toppings) {
        this.toppings = new ArrayList<>(toppings);
    }
}
