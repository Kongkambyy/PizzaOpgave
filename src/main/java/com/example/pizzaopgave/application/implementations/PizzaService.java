package com.example.pizzaopgave.application.implementations;

import com.example.pizzaopgave.application.interfaces.IPizzaService;
import com.example.pizzaopgave.domain.Pizza;
import com.example.pizzaopgave.domain.Topping;

import java.util.List;
import java.util.Optional;

public class PizzaService implements IPizzaService {

    private final IPizzaRepository pizzaRepository;
    private final IToppingRepository toppingRepository;

    public PizzaService(IPizzaRepository pizzaRepository, IToppingRepository toppingRepository) {
        this.pizzaRepository = pizzaRepository;
        this.toppingRepository = toppingRepository;
    }

    @Override
    public List<Pizza> getAllPizzas() {
        return pizzaRepository.findAll();
    }

    @Override
    public List<Topping> getAllToppings() {
        return toppingRepository.findAll();
    }

    @Override
    public Pizza createCustomPizza(String name, String description, double basePrice) {
        Pizza pizza = new Pizza(name, description, basePrice);
        return pizzaRepository.save(pizza);
    }

    @Override
    public Pizza addToppingToPizza(Long pizzaId, Long toppingId) {
        Optional<Pizza> pizzaOptional = pizzaRepository.findById(pizzaId);
        Optional<Topping> toppingOptional = toppingRepository.findById(toppingId);

        if (pizzaOptional.isEmpty()) {
            throw new IllegalArgumentException("Pizza not found");
        }

        if (toppingOptional.isEmpty()) {
            throw new IllegalArgumentException("Topping not found");
        }

        Pizza pizza = pizzaOptional.get();
        Topping topping = toppingOptional.get();

        pizza.addTopping(topping);

        return pizzaRepository.save(pizza);
    }

    @Override
    public Pizza removeToppingFromPizza(Long pizzaId, Long toppingId) {
        Optional<Pizza> pizzaOptional = pizzaRepository.findById(pizzaId);
        Optional<Topping> toppingOptional = toppingRepository.findById(toppingId);

        if (pizzaOptional.isEmpty()) {
            throw new IllegalArgumentException("Pizza not found");
        }

        if (toppingOptional.isEmpty()) {
            throw new IllegalArgumentException("Topping not found");
        }

        Pizza pizza = pizzaOptional.get();
        Topping topping = toppingOptional.get();

        pizza.removeTopping(topping);

        return pizzaRepository.save(pizza);
    }

    @Override
    public Optional<Pizza> getPizzaById(Long pizzaId) {
        return pizzaRepository.findById(pizzaId);
    }
}
