package com.example.pizzaopgave.infrastructure.database;

import com.example.pizzaopgave.domain.User;
import com.example.pizzaopgave.infrastructure.database.DatabaseConfig;
import com.example.pizzaopgave.infrastructure.interfaces.IOrderRepository;
import com.example.pizzaopgave.infrastructure.interfaces.IPizzaRepository;
import com.example.pizzaopgave.infrastructure.interfaces.IUserRepository;
import org.springframework.core.annotation.Order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderRepository implements IOrderRepository {

    private final DatabaseConfig databaseConfig;
    private final IUserRepository userRepository;
    private final IPizzaRepository pizzaRepository;

    public OrderRepository(DatabaseConfig databaseConfig, IUserRepository userRepository, IPizzaRepository pizzaRepository) {
        this.databaseConfig = databaseConfig;
        this.userRepository = userRepository;
        this.pizzaRepository = pizzaRepository;
        createTables();
    }

    private void createTables() {
        String ordersSql = "CREATE TABLE IF NOT EXISTS orders (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY," +
                "user_id BIGINT NOT NULL," +
                "order_date TIMESTAMP NOT NULL," +
                "total_price DOUBLE NOT NULL," +
                "is_completed BOOLEAN NOT NULL," +
                "earned_bonus_points INT NOT NULL," +
                "FOREIGN KEY (user_id) REFERENCES users(id)" +
                ")";

        String orderPizzasSql = "CREATE TABLE IF NOT EXISTS order_pizzas (" +
                "order_id BIGINT NOT NULL," +
                "pizza_id BIGINT NOT NULL," +
                "PRIMARY KEY (order_id, pizza_id)," +
                "FOREIGN KEY (order_id) REFERENCES orders(id)," +
                "FOREIGN KEY (pizza_id) REFERENCES pizzas(id)" +
                ")";

        try (Connection conn = databaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(ordersSql);
            stmt.execute(orderPizzasSql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create order tables", e);
        }
    }

    @Override
    public Order save(Order order) {
        if (order.getId() == null) {
            return insert(order);
        } else {
            return update(order);
        }
    }

    private Order insert(Order order) {
        String sql = "INSERT INTO orders (user_id, order_date, total_price, is_completed, earned_bonus_points) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setLong(1, order.getUser().getId());
            pstmt.setTimestamp(2, Timestamp.valueOf(order.getOrderDate()));
            pstmt.setDouble(3, order.getTotalPrice());
            pstmt.setBoolean(4, order.isCompleted());
            pstmt.setInt(5, order.getEarnedBonusPoints());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating order failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    order.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating order failed, no ID obtained.");
                }
            }

            saveOrderPizzas(order);

            return order;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert order", e);
        }
    }

    private Order update(Order order) {
        String sql = "UPDATE orders SET user_id = ?, order_date = ?, total_price = ?, is_completed = ?, earned_bonus_points = ? WHERE id = ?";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, order.getUser().getId());
            pstmt.setTimestamp(2, Timestamp.valueOf(order.getOrderDate()));
            pstmt.setDouble(3, order.getTotalPrice());
            pstmt.setBoolean(4, order.isCompleted());
            pstmt.setInt(5, order.getEarnedBonusPoints());
            pstmt.setLong(6, order.getId());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating order failed, no rows affected.");
            }

            // Update order-pizzas
            deleteOrderPizzas(order.getId());
            saveOrderPizzas(order);

            return order;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update order", e);
        }
    }

    private void saveOrderPizzas(Order order) {
        String sql = "INSERT INTO order_pizzas (order_id, pizza_id) VALUES (?, ?)";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Pizza pizza : order.getPizzas()) {
                pstmt.setLong(1, order.getId());
                pstmt.setLong(2, pizza.getId());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save order pizzas", e);
        }
    }

    private void deleteOrderPizzas(Long orderId) {
        String sql = "DELETE FROM order_pizzas WHERE order_id = ?";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, orderId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete order pizzas", e);
        }
    }

    @Override
    public Optional<Order> findById(Long id) {
        String sql = "SELECT * FROM orders WHERE id = ?";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Long userId = rs.getLong("user_id");
                    Optional<User> userOptional = userRepository.findById(userId);

                    if (userOptional.isEmpty()) {
                        throw new SQLException("Order's user not found.");
                    }

                    Order order = new Order(
                            rs.getLong("id"),
                            userOptional.get(),
                            rs.getTimestamp("order_date").toLocalDateTime(),
                            rs.getDouble("total_price"),
                            rs.getBoolean("is_completed"),
                            rs.getInt("earned_bonus_points")
                    );

                    // Load pizzas
                    loadOrderPizzas(order);

                    return Optional.of(order);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find order by ID", e);
        }
    }

    private void loadOrderPizzas(Order order) {
        String sql = "SELECT pizza_id FROM order_pizzas WHERE order_id = ?";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, order.getId());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Long pizzaId = rs.getLong("pizza_id");
                    pizzaRepository.findById(pizzaId).ifPresent(order::addPizza);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load order pizzas", e);
        }
    }

    @Override
    public List<Order> findByUser(User user) {
        String sql = "SELECT * FROM orders WHERE user_id = ?";
        List<Order> orders = new ArrayList<>();

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, user.getId());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order(
                            rs.getLong("id"),
                            user,
                            rs.getTimestamp("order_date").toLocalDateTime(),
                            rs.getDouble("total_price"),
                            rs.getBoolean("is_completed"),
                            rs.getInt("earned_bonus_points")
                    );

                    // Load pizzas
                    loadOrderPizzas(order);

                    orders.add(order);
                }
            }
            return orders;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find orders by user", e);
        }
    }

    @Override
    public List<Order> findAll() {
        String sql = "SELECT * FROM orders";
        List<Order> orders = new ArrayList<>();

        try (Connection conn = databaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Long userId = rs.getLong("user_id");
                Optional<User> userOptional = userRepository.findById(userId);

                if (userOptional.isEmpty()) {
                    throw new SQLException("Order's user not found.");
                }

                Order order = new Order(
                        rs.getLong("id"),
                        userOptional.get(),
                        rs.getTimestamp("order_date").toLocalDateTime(),
                        rs.getDouble("total_price"),
                        rs.getBoolean("is_completed"),
                        rs.getInt("earned_bonus_points")
                );

                // Load pizzas
                loadOrderPizzas(order);

                orders.add(order);
            }
            return orders;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all orders", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        // First delete order-pizzas
        deleteOrderPizzas(id);

        // Then delete order
        String sql = "DELETE FROM orders WHERE id = ?";

        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete order", e);
        }
    }
}