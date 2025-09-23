package com.shop.database;

import com.shop.model.Product;
import java.sql.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DatabaseManager {
    private static final String URL = "jdbc:postgresql://localhost:5432/shop_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "97918040gR";

    public DatabaseManager() {
        initializeDatabase();
    }

    private Connection getConnection() throws SQLException {
        Properties props = new Properties();
        props.setProperty("user", USER);
        props.setProperty("password", PASSWORD);
        props.setProperty("ssl", "false");
        return DriverManager.getConnection(URL, props);
    }

    private void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = "CREATE TABLE IF NOT EXISTS products (" +
                    "id SERIAL PRIMARY KEY, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "category VARCHAR(100) NOT NULL, " +
                    "price NUMERIC(10,2) NOT NULL, " +
                    "quantity INTEGER NOT NULL, " +
                    "expiration_date DATE, " +
                    "manufacturer VARCHAR(255))";

            stmt.execute(sql);
            System.out.println("База данных инициализирована успешно");

        } catch (SQLException e) {
            System.out.println("Ошибка инициализации базы данных: " + e.getMessage());
        }
    }
    public boolean addProduct(Product product) {
        String sql = "INSERT INTO products(name, category, price, quantity, expiration_date, manufacturer) " +
                "VALUES(?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getCategory());
            pstmt.setBigDecimal(3, product.getPrice());
            pstmt.setInt(4, product.getQuantity());
            pstmt.setDate(5, Date.valueOf(product.getExpirationDate()));
            pstmt.setString(6, product.getManufacturer());

            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Ошибка добавления товара: " + e.getMessage());
            return false;
        }
    }

    // 1. Поиск по категории
    public List<Product> findByCategory(String category) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE category = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, category);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                products.add(createProductFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.out.println("Ошибка поиска по категории: " + e.getMessage());
        }

        return products;
    }
    // 2. Поиск по цене (диапазон)
    public List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE price BETWEEN ? AND ? ORDER BY price";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBigDecimal(1, minPrice);
            pstmt.setBigDecimal(2, maxPrice);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                products.add(createProductFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.out.println("Ошибка поиска по цене: " + e.getMessage());
        }

        return products;
    }
    // 3. Поиск по количеству (меньше указанного)
    public List<Product> findByQuantityLessThan(int maxQuantity) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE quantity < ? ORDER BY quantity";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maxQuantity);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                products.add(createProductFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.out.println("Ошибка поиска по количеству: " + e.getMessage());
        }

        return products;
    }
    // 4. Поиск по производителю
    public List<Product> findByManufacturer(String manufacturer) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE manufacturer ILIKE ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + manufacturer + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                products.add(createProductFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.out.println("Ошибка поиска по производителю: " + e.getMessage());
        }

        return products;
    }
    // 5. Поиск по сроку годности (до указанной даты)
    public List<Product> findByExpirationDateBefore(LocalDate date) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE expiration_date <= ? ORDER BY expiration_date";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                products.add(createProductFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.out.println("Ошибка поиска по сроку годности: " + e.getMessage());
        }

        return products;
    }

    // 6. Поиск по названию (частичное совпадение)
    public List<Product> findByName(String name) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE name ILIKE ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + name + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                products.add(createProductFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.out.println("Ошибка поиска по названию: " + e.getMessage());
        }

        return products;
    }

    // 7. Поиск товаров с максимальной ценой в категории
    public List<Product> findMostExpensiveInCategory(String category) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE category = ? AND price = " +
                "(SELECT MAX(price) FROM products WHERE category = ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, category);
            pstmt.setString(2, category);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                products.add(createProductFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.out.println("Ошибка поиска самых дорогих товаров: " + e.getMessage());
        }

        return products;
    }

    // 8. Дополнительный запрос: поиск по нескольким параметрам
    public List<Product> findByMultipleCriteria(String category, BigDecimal maxPrice, int minQuantity) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE category = ? AND price <= ? AND quantity >= ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, category);
            pstmt.setBigDecimal(2, maxPrice);
            pstmt.setInt(3, minQuantity);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                products.add(createProductFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.out.println("Ошибка комплексного поиска: " + e.getMessage());
        }

        return products;
    }

    // Получение всех товаров
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products ORDER BY id";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                products.add(createProductFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.out.println("Ошибка получения всех товаров: " + e.getMessage());
        }

        return products;
    }

    // Обновление товара
    public boolean updateProduct(Product product) {
        String sql = "UPDATE products SET name = ?, category = ?, price = ?, quantity = ?, " +
                "expiration_date = ?, manufacturer = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getCategory());
            pstmt.setBigDecimal(3, product.getPrice());
            pstmt.setInt(4, product.getQuantity());
            pstmt.setDate(5, Date.valueOf(product.getExpirationDate()));
            pstmt.setString(6, product.getManufacturer());
            pstmt.setInt(7, product.getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Ошибка обновления товара: " + e.getMessage());
            return false;
        }
    }

    // Удаление товара
    public boolean deleteProduct(int id) {
        String sql = "DELETE FROM products WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Ошибка удаления товара: " + e.getMessage());
            return false;
        }
    }

    private Product createProductFromResultSet(ResultSet rs) throws SQLException {
        return new Product(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("category"),
                rs.getBigDecimal("price"),
                rs.getInt("quantity"),
                rs.getDate("expiration_date").toLocalDate(),
                rs.getString("manufacturer")
        );
    }

    // Тестовое подключение к базе
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.out.println("Ошибка подключения: " + e.getMessage());
            return false;
        }
    }
}