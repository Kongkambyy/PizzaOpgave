package com.example.pizzaopgave.infrastructure.database;

import com.example.pizzaopgave.domain.Pizza;
import com.example.pizzaopgave.domain.Topping;
import com.example.pizzaopgave.infrastructure.interfaces.IPizzaRepository;
import com.example.pizzaopgave.infrastructure.interfaces.IToppingRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PizzaRepository implements IPizzaRepository {

    private final DatabaseConfig databaseConfig;
    private final IToppingRepository toppingRepository;

    public PizzaRepository(DatabaseConfig databaseConfig, IToppingRepository toppingRepository) {
        this.databaseConfig = databaseConfig;
        this.toppingRepository = toppingRepository;
        createTables();
    }

    private void createTables() {
        String pizzaSql = "CREATE TABLE IF NOT EXISTS pizzas (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "name VARCHAR(255) NOT NULL," +
                "description VARCHAR(255) NOT NULL," +
                "base_price DOUBLE NOT NULL" +
                ")";

        String pizzaToppingsSql = "CREATE TABLE IF NOT EXISTS pizza_toppings (" +
                "pizza_id BIGINT NOT NULL," +
                "topping_id BIGINT NOT NULL," +
                "PRIMARY KEY (pizza_id, topping_id)," +
                "FOREIGN KEY (pizza_id) REFERENCES pizzas(id)," +
                "FOREIGN KEY (topping_id) REFERENCES toppings(id)" +
                ")";

        try (Connection conn = databaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(pizzaSql);
            stmt.execute(pizzaToppingsSql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create pizza tables", e);
        }
    }

    @Override
    public Pizza save(Pizza pizza) {
        if (pizza.getId() == null) {
            return insert(pizza);
        } else {
            return update(pizza);
        }
    }

    private Pizza insert(Pizza pizza) {
        String sql = "INSERT INTO pizzas (name, description, base_price) VALUES (?, ?, ?)";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, pizza.getName());
            pstmt.setString(2, pizza.getDescription());
            pstmt.setDouble(3, pizza.getBasePrice());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating pizza failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    pizza.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating pizza failed, no ID obtained.");
                }
            }

            savePizzaToppings(pizza);

            return pizza;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert pizza", e);
        }
    }

    private Pizza update(Pizza pizza) {
        String sql = "UPDATE pizzas SET name = ?, description = ?, base_price = ? WHERE id = ?";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, pizza.getName());
            pstmt.setString(2, pizza.getDescription());
            pstmt.setDouble(3, pizza.getBasePrice());
            pstmt.setLong(4, pizza.getId());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating pizza failed, no rows affected.");
            }

            // Update pizza-toppings
            deletePizzaToppings(pizza.getId());
            savePizzaToppings(pizza);

            return pizza;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update pizza", e);
        }
    }

    private void savePizzaToppings(Pizza pizza) {
        String sql = "INSERT INTO pizza_toppings (pizza_id, topping_id) VALUES (?, ?)";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Topping topping : pizza.getToppings()) {
                pstmt.setLong(1, pizza.getId());
                pstmt.setLong(2, topping.getId());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save pizza toppings", e);
        }
    }

    private void deletePizzaToppings(Long pizzaId) {
        String sql = "DELETE FROM pizza_toppings WHERE pizza_id = ?";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, pizzaId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete pizza toppings", e);
        }
    }

    @Override
    public Optional<Pizza> findById(Long id) {
        String sql = "SELECT * FROM pizzas WHERE id = ?";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Pizza pizza = new Pizza(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getDouble("base_price")
                    );

                    // Load toppings
                    loadPizzaToppings(pizza);

                    return Optional.of(pizza);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find pizza by ID", e);
        }
    }

    private void loadPizzaToppings(Pizza pizza) {
        String sql = "SELECT topping_id FROM pizza_toppings WHERE pizza_id = ?";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, pizza.getId());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Long toppingId = rs.getLong("topping_id");
                    toppingRepository.findById(toppingId).ifPresent(pizza::addTopping);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load pizza toppings", e);
        }
    }

    @Override
    public List<Pizza> findAll() {
        String sql = "SELECT * FROM pizzas";
        List<Pizza> pizzas = new ArrayList<>();

        try (Connection conn = databaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Pizza pizza = new Pizza(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("base_price")
                );

                // Load toppings
                loadPizzaToppings(pizza);

                pizzas.add(pizza);
            }
            return pizzas;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all pizzas", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        // First delete pizza-toppings
        deletePizzaToppings(id);

        // Then delete pizza
        String sql = "DELETE FROM pizzas WHERE id = ?";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete pizza", e);
        }
    }
}