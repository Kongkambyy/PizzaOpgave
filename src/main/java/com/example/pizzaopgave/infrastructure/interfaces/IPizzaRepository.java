package com.example.pizzaopgave.infrastructure.interfaces;

import com.example.pizzaopgave.domain.Pizza;

import java.util.List;
import java.util.Optional;

public interface IPizzaRepository {

    Pizza save(Pizza pizza);

    Optional<Pizza> findById(Long id);

    List<Pizza> findAll();

    void deleteById(Long id);
}
