package com.shop.database;

import com.shop.model.Product;
import java.sql.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String URL = "jdbc:postgresql://localhost:5432/shop_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "97918040gR";

    static {
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("Драйвер PostgreSQL зарегистрирован");
        } catch (ClassNotFoundException e) {
            System.out.println("Ошибка: Драйвер PostgreSQL не найден");
        }
    }

    public DatabaseManager() {
        initializeDatabase();
    }

    private Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new SQLException("Не удалось подключиться к базе данных: " + e.getMessage());
        }
    }

    private void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = "CREATE TABLE IF NOT EXISTS products (" +
                    "id SERIAL PRIMARY KEY, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "category VARCHAR(100) NOT NULL, " +
                    "price NUMERIC(10,2) NOT NULL CHECK (price > 0), " +
                    "quantity INTEGER NOT NULL CHECK (quantity >= 0), " +
                    "expiration_date DATE NOT NULL, " +
                    "manufacturer VARCHAR(255) NOT NULL, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";

            stmt.execute(sql);
            System.out.println("База данных инициализирована успешно");

        } catch (SQLException e) {
            System.out.println("Ошибка инициализации БД: " + getErrorMessage(e));
        }
    }

    public boolean addProduct(Product product) {
        if (product == null || !product.isValid()) {
            System.out.println("Ошибка: Некорректные данные товара");
            return false;
        }

        String sql = "INSERT INTO products (name, category, price, quantity, expiration_date, manufacturer) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getCategory());
            pstmt.setBigDecimal(3, product.getPrice());
            pstmt.setInt(4, product.getQuantity());
            pstmt.setDate(5, Date.valueOf(product.getExpirationDate()));
            pstmt.setString(6, product.getManufacturer());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Товар успешно добавлен");
                return true;
            } else {
                System.out.println("Ошибка: Товар не был добавлен");
                return false;
            }

        } catch (SQLException e) {
            System.out.println("Ошибка при добавлении товара: " + getErrorMessage(e));
            return false;
        }
    }

    public List<Product> findByCategory(String category) {
        List<Product> products = new ArrayList<>();
        if (category == null || category.trim().isEmpty()) {
            System.out.println("Ошибка: Категория не может быть пустой");
            return products;
        }

        String sql = "SELECT * FROM products WHERE category ILIKE ? ORDER BY name";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + category.trim() + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                products.add(createProductFromResultSet(rs));
            }

            System.out.println("Найдено товаров в категории '" + category + "': " + products.size());

        } catch (SQLException e) {
            System.out.println("Ошибка поиска по категории: " + getErrorMessage(e));
        }

        return products;
    }

    public List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        List<Product> products = new ArrayList<>();
        if (minPrice == null || maxPrice == null || minPrice.compareTo(maxPrice) > 0) {
            System.out.println("Ошибка: Некорректный диапазон цен");
            return products;
        }

        String sql = "SELECT * FROM products WHERE price BETWEEN ? AND ? ORDER BY price";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBigDecimal(1, minPrice);
            pstmt.setBigDecimal(2, maxPrice);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                products.add(createProductFromResultSet(rs));
            }

            System.out.printf("Найдено товаров в диапазоне %.2f-%.2f: %d\n",
                    minPrice, maxPrice, products.size());

        } catch (SQLException e) {
            System.out.println("Ошибка поиска по цене: " + getErrorMessage(e));
        }

        return products;
    }

    public List<Product> findByQuantityLessThan(int maxQuantity) {
        List<Product> products = new ArrayList<>();
        if (maxQuantity < 0) {
            System.out.println("Ошибка: Количество не может быть отрицательным");
            return products;
        }

        String sql = "SELECT * FROM products WHERE quantity < ? ORDER BY quantity";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maxQuantity);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                products.add(createProductFromResultSet(rs));
            }

            System.out.printf("Найдено товаров с количеством < %d: %d\n", maxQuantity, products.size());

        } catch (SQLException e) {
            System.out.println("Ошибка поиска по количеству: " + getErrorMessage(e));
        }

        return products;
    }

    public List<Product> findByManufacturer(String manufacturer) {
        List<Product> products = new ArrayList<>();
        if (manufacturer == null || manufacturer.trim().isEmpty()) {
            System.out.println("Ошибка: Производитель не может быть пустым");
            return products;
        }

        String sql = "SELECT * FROM products WHERE manufacturer ILIKE ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + manufacturer.trim() + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                products.add(createProductFromResultSet(rs));
            }

            System.out.println("Найдено товаров производителя '" + manufacturer + "': " + products.size());

        } catch (SQLException e) {
            System.out.println("Ошибка поиска по производителю: " + getErrorMessage(e));
        }

        return products;
    }

    public List<Product> findByExpirationDateBefore(LocalDate date) {
        List<Product> products = new ArrayList<>();
        if (date == null) {
            System.out.println("Ошибка: Дата не может быть пустой");
            return products;
        }

        String sql = "SELECT * FROM products WHERE expiration_date <= ? ORDER BY expiration_date";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                products.add(createProductFromResultSet(rs));
            }

            System.out.println("Найдено товаров с сроком годности до " + date + ": " + products.size());

        } catch (SQLException e) {
            System.out.println("Ошибка поиска по сроку годности: " + getErrorMessage(e));
        }

        return products;
    }

    public List<Product> findByName(String name) {
        List<Product> products = new ArrayList<>();
        if (name == null || name.trim().isEmpty()) {
            System.out.println("Ошибка: Название не может быть пустым");
            return products;
        }

        String sql = "SELECT * FROM products WHERE name ILIKE ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + name.trim() + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                products.add(createProductFromResultSet(rs));
            }

            System.out.println("Найдено товаров с названием '" + name + "': " + products.size());

        } catch (SQLException e) {
            System.out.println("Ошибка поиска по названию: " + getErrorMessage(e));
        }

        return products;
    }

    public List<Product> findMostExpensiveInCategory(String category) {
        List<Product> products = new ArrayList<>();
        if (category == null || category.trim().isEmpty()) {
            System.out.println("Ошибка: Категория не может быть пустой");
            return products;
        }

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

            System.out.println("Найдено самых дорогих товаров в категории '" + category + "': " + products.size());

        } catch (SQLException e) {
            System.out.println("Ошибка поиска самых дорогих товаров: " + getErrorMessage(e));
        }

        return products;
    }

    public List<Product> findByMultipleCriteria(String category, BigDecimal maxPrice, int minQuantity) {
        List<Product> products = new ArrayList<>();
        if (category == null || maxPrice == null || minQuantity < 0) {
            System.out.println("Ошибка: Некорректные параметры поиска");
            return products;
        }

        String sql = "SELECT * FROM products WHERE category ILIKE ? AND price <= ? AND quantity >= ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + category + "%");
            pstmt.setBigDecimal(2, maxPrice);
            pstmt.setInt(3, minQuantity);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                products.add(createProductFromResultSet(rs));
            }

            System.out.printf("Найдено товаров по комплексному запросу: %d\n", products.size());

        } catch (SQLException e) {
            System.out.println("Ошибка комплексного поиска: " + getErrorMessage(e));
        }

        return products;
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products ORDER BY id";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                products.add(createProductFromResultSet(rs));
            }

            System.out.println("Загружено всех товаров: " + products.size());

        } catch (SQLException e) {
            System.out.println("Ошибка получения всех товаров: " + getErrorMessage(e));
        }

        return products;
    }

    public boolean updateProduct(Product product) {
        if (product == null || !product.isValid()) {
            System.out.println("Ошибка: Некорректные данные товара");
            return false;
        }

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
            if (rowsAffected > 0) {
                System.out.println("Товар успешно обновлен");
                return true;
            } else {
                System.out.println("Товар с ID " + product.getId() + " не найден");
                return false;
            }

        } catch (SQLException e) {
            System.out.println("Ошибка обновления товара: " + getErrorMessage(e));
            return false;
        }
    }

    public boolean deleteProduct(int id) {
        if (id <= 0) {
            System.out.println("Ошибка: Некорректный ID товара");
            return false;
        }

        String sql = "DELETE FROM products WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Товар с ID " + id + " успешно удален");
                return true;
            } else {
                System.out.println("Товар с ID " + id + " не найден");
                return false;
            }

        } catch (SQLException e) {
            System.out.println("Ошибка удаления товара: " + getErrorMessage(e));
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

    private String getErrorMessage(SQLException e) {
        switch (e.getSQLState()) {
            case "08001": return "Не удалось подключиться к базе данных";
            case "28000": return "Неверный логин или пароль";
            case "3D000": return "База данных не существует";
            case "23505": return "Товар с таким ID уже существует";
            case "23514": return "Некорректные данные (нарушение CHECK ограничений)";
            default: return e.getMessage();
        }
    }

    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.out.println("Ошибка подключения: " + getErrorMessage(e));
            return false;
        }
    }
}