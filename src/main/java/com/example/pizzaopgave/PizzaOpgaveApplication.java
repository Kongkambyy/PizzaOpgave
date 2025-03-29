package com.example.pizzaopgave;

import com.example.pizzaopgave.application.implementations.OrderService;
import com.example.pizzaopgave.application.implementations.PizzaService;
import com.example.pizzaopgave.application.implementations.UserService;
import com.example.pizzaopgave.application.interfaces.IOrderService;
import com.example.pizzaopgave.application.interfaces.IPizzaService;
import com.example.pizzaopgave.application.interfaces.IUserService;
import com.example.pizzaopgave.infrastructure.database.*;
import com.example.pizzaopgave.infrastructure.interfaces.IOrderRepository;
import com.example.pizzaopgave.infrastructure.interfaces.IPizzaRepository;
import com.example.pizzaopgave.infrastructure.interfaces.IToppingRepository;
import com.example.pizzaopgave.infrastructure.interfaces.IUserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PizzaOpgaveApplication {

    public static void main(String[] args) {
        // Initialize the database configuration
        DatabaseConfig.initialize("jdbc:h2:mem:pizzaparadise", "sa", "");

        SpringApplication.run(PizzaOpgaveApplication.class, args);
    }

    @Bean
    public DatabaseConfig databaseConfig() {
        return DatabaseConfig.getInstance();
    }

    @Bean
    public IUserRepository userRepository(DatabaseConfig databaseConfig) {
        return new UserRepository(databaseConfig);
    }

    @Bean
    public IToppingRepository toppingRepository(DatabaseConfig databaseConfig) {
        return new ToppingRepository(databaseConfig);
    }

    @Bean
    public IPizzaRepository pizzaRepository(DatabaseConfig databaseConfig, IToppingRepository toppingRepository) {
        return new PizzaRepository(databaseConfig, toppingRepository);
    }

    @Bean
    public IOrderRepository orderRepository(DatabaseConfig databaseConfig, IUserRepository userRepository, IPizzaRepository pizzaRepository) {
        return new OrderRepository(databaseConfig, userRepository, pizzaRepository);
    }

    @Bean
    public IUserService userService(IUserRepository userRepository, IOrderRepository orderRepository) {
        return new UserService(userRepository, orderRepository);
    }

    @Bean
    public IPizzaService pizzaService(IPizzaRepository pizzaRepository, IToppingRepository toppingRepository) {
        return new PizzaService(pizzaRepository, toppingRepository);
    }

    @Bean
    public IOrderService orderService(IOrderRepository orderRepository, IUserRepository userRepository, IPizzaRepository pizzaRepository) {
        return new OrderService(orderRepository, userRepository, pizzaRepository);
    }

    @Bean
    public void initializeData(IToppingRepository toppingRepository, IPizzaService pizzaService, IUserService userService) {

        toppingRepository.save(new dk.pizzaparadise.domain.Topping("Cheese", 5.0));
        toppingRepository.save(new dk.pizzaparadise.domain.Topping("Pepperoni", 10.0));
        toppingRepository.save(new dk.pizzaparadise.domain.Topping("Mushrooms", 8.0));
        toppingRepository.save(new dk.pizzaparadise.domain.Topping("Onions", 5.0));
        toppingRepository.save(new dk.pizzaparadise.domain.Topping("Bell Peppers", 7.0));
        toppingRepository.save(new dk.pizzaparadise.domain.Topping("Olives", 6.0));
        toppingRepository.save(new dk.pizzaparadise.domain.Topping("Ham", 12.0));
        toppingRepository.save(new dk.pizzaparadise.domain.Topping("Pineapple", 8.0));

        // Create some pizzas
        dk.pizzaparadise.domain.Pizza margherita = pizzaService.createCustomPizza("Margherita", "Classic pizza with tomato sauce and cheese", 50.0);
        pizzaService.addToppingToPizza(margherita.getId(), 1L); // Cheese

        dk.pizzaparadise.domain.Pizza pepperoni = pizzaService.createCustomPizza("Pepperoni", "Pizza with tomato sauce, cheese, and pepperoni", 60.0);
        pizzaService.addToppingToPizza(pepperoni.getId(), 1L); // Cheese
        pizzaService.addToppingToPizza(pepperoni.getId(), 2L); // Pepperoni

        dk.pizzaparadise.domain.Pizza vegetarian = pizzaService.createCustomPizza("Vegetarian", "Pizza with tomato sauce, cheese, and various vegetables", 65.0);
        pizzaService.addToppingToPizza(vegetarian.getId(), 1L); // Cheese
        pizzaService.addToppingToPizza(vegetarian.getId(), 3L); // Mushrooms
        pizzaService.addToppingToPizza(vegetarian.getId(), 4L); // Onions
        pizzaService.addToppingToPizza(vegetarian.getId(), 5L); // Bell Peppers
        pizzaService.addToppingToPizza(vegetarian.getId(), 6L); // Olives

        dk.pizzaparadise.domain.Pizza hawaiian = pizzaService.createCustomPizza("Hawaiian", "Pizza with tomato sauce, cheese, ham, and pineapple", 70.0);
        pizzaService.addToppingToPizza(hawaiian.getId(), 1L); // Cheese
        pizzaService.addToppingToPizza(hawaiian.getId(), 7L); // Ham
        pizzaService.addToppingToPizza(hawaiian.getId(), 8L); // Pineapple

        // Create a test user
        dk.pizzaparadise.domain.User user = new dk.pizzaparadise.domain.User("Test User", "test@example.com", "password", "123 Test Street");
        userService.createUser(user);
    }
}