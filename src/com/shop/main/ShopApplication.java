package com.shop.main;

import com.shop.database.DatabaseManager;
import com.shop.model.Product;
import com.shop.util.InputValidator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class ShopApplication {
    private DatabaseManager dbManager;
    private Scanner scanner;
    private boolean isRunning;

    public ShopApplication() {
        this.scanner = new Scanner(System.in);
        this.isRunning = true;
        initializeDatabase();
    }

    private void initializeDatabase() {
        try {
            this.dbManager = new DatabaseManager();
            if (!dbManager.testConnection()) {
                System.out.println("Не удалось подключиться к базе данных");
                System.exit(1);
            }
        } catch (Exception e) {
            System.out.println("Ошибка инициализации: " + e.getMessage());
            System.exit(1);
        }
    }

    public void run() {
        System.out.println("\n");
        System.out.println("СИСТЕМА УПРАВЛЕНИЯ МАГАЗИНОМ");

        while (isRunning) {
            try {
                showMainMenu();
                int choice = InputValidator.readMenuChoice(scanner, 0, 12);
                processMenuChoice(choice);
            } catch (Exception e) {
                System.out.println("Неожиданная ошибка: " + e.getMessage());
            }

            if (isRunning) {
                System.out.println("\n Нажмите Enter для продолжения...");
                scanner.nextLine();
            }
        }

        shutdown();
    }

    private void showMainMenu() {
        System.out.println("\n");
        System.out.println("ГЛАВНОЕ МЕНЮ");
        System.out.println("1. Добавить товар");
        System.out.println("2. Поиск по категории");
        System.out.println("3. Поиск по диапазону цен");
        System.out.println("4. Поиск по количеству (мало на складе)");
        System.out.println("5. Поиск по производителю");
        System.out.println("6. Поиск по сроку годности");
        System.out.println("7. Поиск по названию");
        System.out.println("8. Самые дорогие в категории");
        System.out.println("9. Комплексный поиск");
        System.out.println("10. Показать все товары");
        System.out.println("11. Обновить товар");
        System.out.println("12. Удалить товар");
        System.out.println("0. Выход");
        System.out.println("─".repeat(50));
    }

    private void processMenuChoice(int choice) {
        switch (choice) {
            case 1 -> addProduct();
            case 2 -> searchByCategory();
            case 3 -> searchByPriceRange();
            case 4 -> searchByQuantity();
            case 5 -> searchByManufacturer();
            case 6 -> searchByExpirationDate();
            case 7 -> searchByName();
            case 8 -> searchMostExpensiveInCategory();
            case 9 -> searchByMultipleCriteria();
            case 10 -> showAllProducts();
            case 11 -> updateProduct();
            case 12 -> deleteProduct();
            case 0 -> isRunning = false;
            default -> System.out.println("Неизвестный пункт меню");
        }
    }

    private void addProduct() {
        System.out.println("\n" + "ДОБАВЛЕНИЕ НОВОГО ТОВАРА");
        System.out.println("─".repeat(30));

        try {
            String name = InputValidator.readString(scanner, "Название товара: ", 2, 100);
            String category = InputValidator.readString(scanner, "Категория: ", 2, 50);
            BigDecimal price = InputValidator.readBigDecimal(scanner, "Цена: ",
                    BigDecimal.ZERO, new BigDecimal("1000000"));
            int quantity = InputValidator.readInt(scanner, "Количество: ", 0, 1000000);
            LocalDate expDate = InputValidator.readDate(scanner, "Срок годности (ГГГГ-ММ-ДД): ");
            String manufacturer = InputValidator.readString(scanner, "Производитель: ", 2, 100);

            Product product = new Product(0, name, category, price, quantity, expDate, manufacturer);
            dbManager.addProduct(product);

        } catch (Exception e) {
            System.out.println("Ошибка при добавлении товара: " + e.getMessage());
        }
    }

    private void searchByCategory() {
        System.out.println("\n ПОИСК ПО КАТЕГОРИИ");
        String category = InputValidator.readString(scanner, "Введите категорию: ", 1, 50);
        List<Product> products = dbManager.findByCategory(category);
        printResults(products);
    }

    private void searchByPriceRange() {
        System.out.println("\n ПОИСК ПО ДИАПАЗОНУ ЦЕН");
        BigDecimal minPrice = InputValidator.readBigDecimal(scanner, "Минимальная цена: ",
                BigDecimal.ZERO, new BigDecimal("1000000"));
        BigDecimal maxPrice = InputValidator.readBigDecimal(scanner, "Максимальная цена: ",
                minPrice, new BigDecimal("1000000"));
        List<Product> products = dbManager.findByPriceRange(minPrice, maxPrice);
        printResults(products);
    }

    private void searchByQuantity() {
        System.out.println("\n ПОИСК ТОВАРОВ С МАЛЫМ ЗАПАСОМ");
        int maxQuantity = InputValidator.readInt(scanner, "Максимальное количество: ", 0, 1000000);
        List<Product> products = dbManager.findByQuantityLessThan(maxQuantity);
        printResults(products);
    }

    private void searchByManufacturer() {
        System.out.println("\n ПОИСК ПО ПРОИЗВОДИТЕЛЮ");
        String manufacturer = InputValidator.readString(scanner, "Введите производителя: ", 1, 100);
        List<Product> products = dbManager.findByManufacturer(manufacturer);
        printResults(products);
    }

    private void searchByExpirationDate() {
        System.out.println("\n ПОИСК ПО СРОКУ ГОДНОСТИ");
        LocalDate date = InputValidator.readDate(scanner, "Введите дату (ГГГГ-ММ-ДД): ");
        List<Product> products = dbManager.findByExpirationDateBefore(date);
        printResults(products);
    }

    private void searchByName() {
        System.out.println("\n ПОИСК ПО НАЗВАНИЮ");
        String name = InputValidator.readString(scanner, "Введите название: ", 1, 100);
        List<Product> products = dbManager.findByName(name);
        printResults(products);
    }

    private void searchMostExpensiveInCategory() {
        System.out.println("\n САМЫЕ ДОРОГИЕ ТОВАРЫ В КАТЕГОРИИ");
        String category = InputValidator.readString(scanner, "Введите категорию: ", 1, 50);
        List<Product> products = dbManager.findMostExpensiveInCategory(category);
        printResults(products);
    }

    private void searchByMultipleCriteria() {
        System.out.println("\n КОМПЛЕКСНЫЙ ПОИСК");
        String category = InputValidator.readString(scanner, "Категория: ", 1, 50);
        BigDecimal maxPrice = InputValidator.readBigDecimal(scanner, "Максимальная цена: ",
                BigDecimal.ZERO, new BigDecimal("1000000"));
        int minQuantity = InputValidator.readInt(scanner, "Минимальное количество: ", 0, 1000000);
        List<Product> products = dbManager.findByMultipleCriteria(category, maxPrice, minQuantity);
        printResults(products);
    }

    private void showAllProducts() {
        System.out.println("\n=== ВСЕ ТОВАРЫ ===");
        List<Product> products = dbManager.getAllProducts();
        printResults(products);
    }

    private void updateProduct() {
        System.out.println("\n ОБНОВЛЕНИЕ ТОВАРА");
        System.out.println("─".repeat(30));

        try {
            List<Product> allProducts = dbManager.getAllProducts();
            if (allProducts.isEmpty()) {
                System.out.println("️ В базе нет товаров для обновления");
                return;
            }

            System.out.println("Список товаров:");
            printResults(allProducts);

            int productId = InputValidator.readInt(scanner, "Введите ID товара для обновления: ", 1, 1000000);

            Product productToUpdate = null;
            for (Product product : allProducts) {
                if (product.getId() == productId) {
                    productToUpdate = product;
                    break;
                }
            }

            if (productToUpdate == null) {
                System.out.println(" Товар с ID " + productId + " не найден");
                return;
            }

            System.out.println("Текущие данные товара:");
            System.out.println(productToUpdate);
            System.out.println("\nВведите новые данные (оставьте пустым для сохранения текущего значения):");

            String newName = InputValidator.readOptionalString(scanner,
                    "Название товара [" + productToUpdate.getName() + "]: ", 2, 100);
            if (!newName.isEmpty()) productToUpdate.setName(newName);

            String newCategory = InputValidator.readOptionalString(scanner,
                    "Категория [" + productToUpdate.getCategory() + "]: ", 2, 50);
            if (!newCategory.isEmpty()) productToUpdate.setCategory(newCategory);

            BigDecimal newPrice = InputValidator.readOptionalBigDecimal(scanner,
                    "Цена [" + productToUpdate.getPrice() + "]: ", BigDecimal.ZERO, new BigDecimal("1000000"));
            if (newPrice != null) productToUpdate.setPrice(newPrice);

            Integer newQuantity = InputValidator.readOptionalInt(scanner,
                    "Количество [" + productToUpdate.getQuantity() + "]: ", 0, 1000000);
            if (newQuantity != null) productToUpdate.setQuantity(newQuantity);

            LocalDate newExpDate = InputValidator.readOptionalDate(scanner,
                    "Срок годности [" + productToUpdate.getExpirationDate() + "]: ");
            if (newExpDate != null) productToUpdate.setExpirationDate(newExpDate);

            String newManufacturer = InputValidator.readOptionalString(scanner,
                    "Производитель [" + productToUpdate.getManufacturer() + "]: ", 2, 100);
            if (!newManufacturer.isEmpty()) productToUpdate.setManufacturer(newManufacturer);

            if (dbManager.updateProduct(productToUpdate)) {
                System.out.println("Товар успешно обновлен");
            }

        } catch (Exception e) {
            System.out.println("Ошибка при обновлении товара: " + e.getMessage());
        }
    }

    private void deleteProduct() {
        System.out.println("\n УДАЛЕНИЕ ТОВАРА");
        System.out.println("─".repeat(30));

        try {
            List<Product> allProducts = dbManager.getAllProducts();
            if (allProducts.isEmpty()) {
                System.out.println(" В базе нет товаров для удаления");
                return;
            }

            System.out.println("Список товаров:");
            printResults(allProducts);

            int productId = InputValidator.readInt(scanner, "Введите ID товара для удаления: ", 1, 1000000);

            System.out.print("Вы уверены, что хотите удалить товар с ID " + productId + "? (да/нет): ");
            String confirmation = scanner.nextLine().trim().toLowerCase();

            if (confirmation.equals("да") || confirmation.equals("д") || confirmation.equals("y") || confirmation.equals("yes")) {
                if (dbManager.deleteProduct(productId)) {
                    System.out.println("Товар успешно удален");
                }
            } else {
                System.out.println("Удаление отменено");
            }

        } catch (Exception e) {
            System.out.println("Ошибка при удалении товара: " + e.getMessage());
        }
    }

    private void printResults(List<Product> products) {
        if (products.isEmpty()) {
            System.out.println("Товары не найдены.");
            return;
        }

        System.out.println("Найдено товаров: " + products.size());
        System.out.println("----------------------------------------");

        for (Product product : products) {
            System.out.println(product);
            System.out.println("----------------------------------------");
        }
    }

    private void shutdown() {
        System.out.println("\n Завершение работы...");
        try {
            if (scanner != null) scanner.close();
            System.out.println("Система завершила работу корректно");
        } catch (Exception e) {
            System.out.println("Ошибка при завершении работы: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            new ShopApplication().run();
        } catch (Exception e) {
            System.out.println("Критическая ошибка: " + e.getMessage());
        }
    }
}