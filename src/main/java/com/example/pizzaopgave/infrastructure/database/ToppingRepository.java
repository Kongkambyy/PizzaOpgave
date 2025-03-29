package com.example.pizzaopgave.infrastructure.database;

import com.example.pizzaopgave.domain.Topping;
import com.example.pizzaopgave.infrastructure.interfaces.IToppingRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ToppingRepository implements IToppingRepository {

    private final DatabaseConfig databaseConfig;

    public ToppingRepository(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
        createTable();
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS toppings (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "name VARCHAR(255) NOT NULL," +
                "price DOUBLE NOT NULL" +
                ")";

        try (Connection conn = databaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create toppings table", e);
        }
    }

    @Override
    public Topping save(Topping topping) {
        if (topping.getId() == null) {
            return insert(topping);
        } else {
            return update(topping);
        }
    }

    private Topping insert(Topping topping) {
        String sql = "INSERT INTO toppings (name, price) VALUES (?, ?)";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, topping.getName());
            pstmt.setDouble(2, topping.getPrice());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating topping failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    topping.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating topping failed, no ID obtained.");
                }
            }

            return topping;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert topping", e);
        }
    }

    private Topping update(Topping topping) {
        String sql = "UPDATE toppings SET name = ?, price = ? WHERE id = ?";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, topping.getName());
            pstmt.setDouble(2, topping.getPrice());
            pstmt.setLong(3, topping.getId());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating topping failed, no rows affected.");
            }

            return topping;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update topping", e);
        }
    }

    @Override
    public Optional<Topping> findById(Long id) {
        String sql = "SELECT * FROM toppings WHERE id = ?";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Topping topping = new Topping(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getDouble("price")
                    );
                    return Optional.of(topping);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find topping by ID", e);
        }
    }

    @Override
    public List<Topping> findAll() {
        String sql = "SELECT * FROM toppings";
        List<Topping> toppings = new ArrayList<>();

        try (Connection conn = databaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Topping topping = new Topping(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getDouble("price")
                );
                toppings.add(topping);
            }
            return toppings;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all toppings", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM toppings WHERE id = ?";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete topping", e);
        }
    }
}
