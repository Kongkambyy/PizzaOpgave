package com.example.pizzaopgave.application.interfaces;

import com.example.pizzaopgave.domain.Pizza;
import com.example.pizzaopgave.domain.Topping;

import java.util.List;
import java.util.Optional;

public interface IPizzaService {

    List<Pizza> getAllPizzas();

    List<Topping> getAllToppings();

    Pizza createCustomPizza(String name, String description, double basePrice);

    Pizza addToppingToPizza(Long pizzaId, Long toppingId);

    Pizza removeToppingFromPizza(Long pizzaId, Long toppingId);

    Optional<Pizza> getPizzaById(Long pizzaId);
}