package com.shop.main;

import com.shop.database.DatabaseManager;
import com.shop.model.Product;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class ShopApplication {
    private DatabaseManager dbManager;
    private Scanner scanner;

    public ShopApplication() {
        dbManager = new DatabaseManager();
        scanner = new Scanner(System.in);

        if (!dbManager.testConnection()) {
            System.out.println("Не удалось подключиться к базе данных!");
            System.out.println("Проверьте настройки подключения в DatabaseManager");
            System.exit(1);
        }
    }

    public void run() {
        System.out.println("=== СИСТЕМА УПРАВЛЕНИЯ МАГАЗИНОМ (PostgreSQL) ===");

        while (true) {
            printMenu();
            String choice = scanner.nextLine();

            switch (choice) {
                case "1": addProduct(); break;
                case "2": searchByCategory(); break;
                case "3": searchByPriceRange(); break;
                case "4": searchByQuantity(); break;
                case "5": searchByManufacturer(); break;
                case "6": searchByExpirationDate(); break;
                case "7": searchByName(); break;
                case "8": searchMostExpensiveInCategory(); break;
                case "9": searchByMultipleCriteria(); break;
                case "10": showAllProducts(); break;
                case "11": updateProduct(); break;
                case "12": deleteProduct(); break;
                case "0":
                    System.out.println("Выход из программы...");
                    return;
                default:
                    System.out.println("Неверный выбор. Попробуйте снова.");
            }

            System.out.println("\nНажмите Enter для продолжения...");
            scanner.nextLine();
        }
    }
    private void printMenu() {
        System.out.println("\n=== ГЛАВНОЕ МЕНЮ ===");
        System.out.println("1. Добавить товар");
        System.out.println("2. Поиск по категории");
        System.out.println("3. Поиск по диапазону цен");
        System.out.println("4. Поиск по количеству (мало на складе)");
        System.out.println("5. Поиск по производителю");
        System.out.println("6. Поиск по сроку годности");
        System.out.println("7. Поиск по названию");
        System.out.println("8. Самые дорогие товары в категории");
        System.out.println("9. Комплексный поиск");
        System.out.println("10. Показать все товары");
        System.out.println("11. Обновить товар");
        System.out.println("12. Удалить товар");
        System.out.println("0. Выход");
        System.out.print("Выберите действие: ");
    }

    private void addProduct() {
        System.out.println("\n=== ДОБАВЛЕНИЕ ТОВАРА ===");

        Product product = new Product();

        System.out.print("Название товара: ");
        product.setName(scanner.nextLine());

        System.out.print("Категория: ");
        product.setCategory(scanner.nextLine());

        System.out.print("Цена: ");
        product.setPrice(new BigDecimal(scanner.nextLine()));

        System.out.print("Количество: ");
        product.setQuantity(Integer.parseInt(scanner.nextLine()));

        System.out.print("Срок годности (гггг-мм-дд): ");
        product.setExpirationDate(LocalDate.parse(scanner.nextLine()));

        System.out.print("Производитель: ");
        product.setManufacturer(scanner.nextLine());

        if (dbManager.addProduct(product)) {
            System.out.println("Товар успешно добавлен!");
        } else {
            System.out.println("Ошибка при добавлении товара!");
        }
    }

    private void searchByCategory() {
        System.out.println("\n=== ПОИСК ПО КАТЕГОРИИ ===");
        System.out.print("Введите категорию: ");
        String category = scanner.nextLine();

        List<Product> products = dbManager.findByCategory(category);
        printResults(products);
    }

    private void searchByPriceRange() {
        System.out.println("\n=== ПОИСК ПО ДИАПАЗОНУ ЦЕН ===");
        System.out.print("Минимальная цена: ");
        BigDecimal minPrice = new BigDecimal(scanner.nextLine());

        System.out.print("Максимальная цена: ");
        BigDecimal maxPrice = new BigDecimal(scanner.nextLine());

        List<Product> products = dbManager.findByPriceRange(minPrice, maxPrice);
        printResults(products);
    }

    private void searchByQuantity() {
        System.out.println("\n=== ПОИСК ПО КОЛИЧЕСТВУ ===");
        System.out.print("Максимальное количество: ");
        int maxQuantity = Integer.parseInt(scanner.nextLine());

        List<Product> products = dbManager.findByQuantityLessThan(maxQuantity);
        printResults(products);
    }

    private void searchByManufacturer() {
        System.out.println("\n=== ПОИСК ПО ПРОИЗВОДИТЕЛЮ ===");
        System.out.print("Введите производителя: ");
        String manufacturer = scanner.nextLine();

        List<Product> products = dbManager.findByManufacturer(manufacturer);
        printResults(products);
    }

    private void searchByExpirationDate() {
        System.out.println("\n=== ПОИСК ПО СРОКУ ГОДНОСТИ ===");
        System.out.print("Введите дату (гггг-мм-дд): ");
        LocalDate date = LocalDate.parse(scanner.nextLine());

        List<Product> products = dbManager.findByExpirationDateBefore(date);
        printResults(products);
    }

    private void searchByName() {
        System.out.println("\n=== ПОИСК ПО НАЗВАНИю ===");
        System.out.print("Введите название: ");
        String name = scanner.nextLine();

        List<Product> products = dbManager.findByName(name);
        printResults(products);
    }

    private void searchMostExpensiveInCategory() {
        System.out.println("\n=== САМЫЕ ДОРОГИЕ ТОВАРЫ В КАТЕГОРИИ ===");
        System.out.print("Введите категорию: ");
        String category = scanner.nextLine();

        List<Product> products = dbManager.findMostExpensiveInCategory(category);
        printResults(products);
    }

    private void searchByMultipleCriteria() {
        System.out.println("\n=== КОМПЛЕКСНЫЙ ПОИСК ===");
        System.out.print("Категория: ");
        String category = scanner.nextLine();

        System.out.print("Максимальная цена: ");
        BigDecimal maxPrice = new BigDecimal(scanner.nextLine());

        System.out.print("Минимальное количество: ");
        int minQuantity = Integer.parseInt(scanner.nextLine());

        List<Product> products = dbManager.findByMultipleCriteria(category, maxPrice, minQuantity);
        printResults(products);
    }

    private void showAllProducts() {
        System.out.println("\n=== ВСЕ ТОВАРЫ ===");
        List<Product> products = dbManager.getAllProducts();
        printResults(products);
    }

    private void updateProduct() {
        System.out.println("\n=== ОБНОВЛЕНИЕ ТОВАРА ===");
        System.out.print("Введите ID товара для обновления: ");
        int id = Integer.parseInt(scanner.nextLine());

        // Сначала получим текущие данные товара
        List<Product> products = dbManager.getAllProducts();
        Product productToUpdate = products.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);

        if (productToUpdate == null) {
            System.out.println("Товар с ID " + id + " не найден!");
            return;
        }

        System.out.println("Текущие данные: " + productToUpdate);
        System.out.println("Введите новые данные (оставьте пустым для сохранения текущего значения):");

        System.out.print("Название товара [" + productToUpdate.getName() + "]: ");
        String name = scanner.nextLine();
        if (!name.isEmpty()) productToUpdate.setName(name);

        System.out.print("Категория [" + productToUpdate.getCategory() + "]: ");
        String category = scanner.nextLine();
        if (!category.isEmpty()) productToUpdate.setCategory(category);

        System.out.print("Цена [" + productToUpdate.getPrice() + "]: ");
        String priceStr = scanner.nextLine();
        if (!priceStr.isEmpty()) productToUpdate.setPrice(new BigDecimal(priceStr));

        System.out.print("Количество [" + productToUpdate.getQuantity() + "]: ");
        String quantityStr = scanner.nextLine();
        if (!quantityStr.isEmpty()) productToUpdate.setQuantity(Integer.parseInt(quantityStr));

        System.out.print("Срок годности [" + productToUpdate.getExpirationDate() + "]: ");
        String dateStr = scanner.nextLine();
        if (!dateStr.isEmpty()) productToUpdate.setExpirationDate(LocalDate.parse(dateStr));

        System.out.print("Производитель [" + productToUpdate.getManufacturer() + "]: ");
        String manufacturer = scanner.nextLine();
        if (!manufacturer.isEmpty()) productToUpdate.setManufacturer(manufacturer);

        if (dbManager.updateProduct(productToUpdate)) {
            System.out.println("Товар успешно обновлен!");
        } else {
            System.out.println("Ошибка при обновлении товара!");
        }
    }

    private void deleteProduct() {
        System.out.println("\n=== УДАЛЕНИЕ ТОВАРА ===");
        System.out.print("Введите ID товара для удаления: ");
        int id = Integer.parseInt(scanner.nextLine());

        System.out.print("Вы уверены, что хотите удалить товар с ID " + id + "? (y/n): ");
        String confirmation = scanner.nextLine();

        if (confirmation.equalsIgnoreCase("y")) {
            if (dbManager.deleteProduct(id)) {
                System.out.println("Товар успешно удален!");
            } else {
                System.out.println("Ошибка при удалении товара или товар не найден!");
            }
        } else {
            System.out.println("Удаление отменено.");
        }
    }

    private void printResults(List<Product> products) {
        if (products.isEmpty()) {
            System.out.println("Товары не найдены.");
        } else {
            System.out.println("Найдено товаров: " + products.size());
            System.out.println("----------------------------------------");
            for (Product product : products) {
                System.out.println(product);
                System.out.println("----------------------------------------");
            }
        }
    }

    public static void main(String[] args) {
        ShopApplication app = new ShopApplication();
        app.run();
    }
}