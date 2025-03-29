package com.example.pizzaopgave.infrastructure.interfaces;

import com.example.pizzaopgave.domain.Topping;

import java.util.List;
import java.util.Optional;

public interface IToppingRepository {

    Topping save(Topping topping);


    Optional<Topping> findById(Long id);

    List<Topping> findAll();

    void deleteById(Long id);
}