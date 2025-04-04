package com.example.pizzaopgave;

import com.example.pizzaopgave.application.implementations.OrderService;
import com.example.pizzaopgave.application.implementations.PizzaService;
import com.example.pizzaopgave.application.implementations.UserService;
import com.example.pizzaopgave.application.interfaces.IOrderService;
import com.example.pizzaopgave.application.interfaces.IPizzaService;
import com.example.pizzaopgave.application.interfaces.IUserService;
import com.example.pizzaopgave.domain.Pizza;
import com.example.pizzaopgave.domain.Topping;
import com.example.pizzaopgave.domain.User;
import com.example.pizzaopgave.infrastructure.database.DatabaseConfig;
import com.example.pizzaopgave.infrastructure.database.OrderRepository;
import com.example.pizzaopgave.infrastructure.database.PizzaRepository;
import com.example.pizzaopgave.infrastructure.database.ToppingRepository;
import com.example.pizzaopgave.infrastructure.database.UserRepository;
import com.example.pizzaopgave.infrastructure.interfaces.IOrderRepository;
import com.example.pizzaopgave.infrastructure.interfaces.IPizzaRepository;
import com.example.pizzaopgave.infrastructure.interfaces.IToppingRepository;
import com.example.pizzaopgave.infrastructure.interfaces.IUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PizzaOpgaveApplication {

    public static void main(String[] args) {
        DatabaseConfig.initialize("jdbc:mysql://localhost:3306/pizzaparadise", "root", "root");

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
    public CommandLineRunner initializeData(IToppingRepository toppingRepository, IPizzaService pizzaService, IUserService userService) {
        return args -> {
            // Create some toppings
            toppingRepository.save(new Topping("Cheese", 5.0));
            toppingRepository.save(new Topping("Pepperoni", 10.0));
            toppingRepository.save(new Topping("Mushrooms", 8.0));
            toppingRepository.save(new Topping("Onions", 5.0));
            toppingRepository.save(new Topping("Bell Peppers", 7.0));
            toppingRepository.save(new Topping("Olives", 6.0));
            toppingRepository.save(new Topping("Ham", 12.0));
            toppingRepository.save(new Topping("Pineapple", 8.0));

            // Create some pizzas
            Pizza margherita = pizzaService.createCustomPizza("Margherita", "Klassisk pizza med tomat og ost", 50.0);
            pizzaService.addToppingToPizza(margherita.getId(), 1L);

            Pizza pepperoni = pizzaService.createCustomPizza("Pepperoni", "Magharita med pepperoni", 60.0);
            pizzaService.addToppingToPizza(pepperoni.getId(), 1L);
            pizzaService.addToppingToPizza(pepperoni.getId(), 2L);

            Pizza vegetarian = pizzaService.createCustomPizza("Vegetarian", "Skrald pizza, men skal tilbydes", 65.0);
            pizzaService.addToppingToPizza(vegetarian.getId(), 1L);
            pizzaService.addToppingToPizza(vegetarian.getId(), 3L);
            pizzaService.addToppingToPizza(vegetarian.getId(), 4L);
            pizzaService.addToppingToPizza(vegetarian.getId(), 5L);
            pizzaService.addToppingToPizza(vegetarian.getId(), 6L);

            Pizza hawaiian = pizzaService.createCustomPizza("Hawaiian", "Pizza med tomatsauce, ost og ananas", 70.0);
            pizzaService.addToppingToPizza(hawaiian.getId(), 1L);
            pizzaService.addToppingToPizza(hawaiian.getId(), 7L);
            pizzaService.addToppingToPizza(hawaiian.getId(), 8L);

            User user = new User("Test User", "test@example.com", "password", "123 test test");
            userService.createUser(user);
        };
    }
}