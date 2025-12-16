package com.shop.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class InputValidator {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static Integer readInt(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();

                if (input.isEmpty()) {
                    System.out.println("Ошибка: Ввод не может быть пустым");
                    continue;
                }

                int value = Integer.parseInt(input);

                if (value < min) {
                    System.out.printf("Ошибка: Число не может быть меньше %d\n", min);
                    continue;
                }

                if (value > max) {
                    System.out.printf("Ошибка: Число не может быть больше %d\n", max);
                    continue;
                }

                return value;

            } catch (NumberFormatException e) {
                System.out.println("Ошибка: Введите корректное целое число");
            }
        }
    }

    public static Integer readOptionalInt(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();

                if (input.isEmpty()) {
                    return null;
                }

                int value = Integer.parseInt(input);

                if (value < min) {
                    System.out.printf("Ошибка: Число не может быть меньше %d\n", min);
                    continue;
                }

                if (value > max) {
                    System.out.printf("Ошибка: Число не может быть больше %d\n", max);
                    continue;
                }

                return value;

            } catch (NumberFormatException e) {
                System.out.println("Ошибка: Введите корректное целое число");
            }
        }
    }

    public static BigDecimal readBigDecimal(Scanner scanner, String prompt, BigDecimal min, BigDecimal max) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim().replace(',', '.');

                if (input.isEmpty()) {
                    System.out.println("Ошибка: Ввод не может быть пустым");
                    continue;
                }

                BigDecimal value = new BigDecimal(input);

                if (value.compareTo(min) < 0) {
                    System.out.printf("Ошибка: Цена не может быть меньше %.2f\n", min);
                    continue;
                }

                if (value.compareTo(max) > 0) {
                    System.out.printf("Ошибка: Цена не может быть больше %.2f\n", max);
                    continue;
                }

                return value;

            } catch (NumberFormatException e) {
                System.out.println("Ошибка: Введите корректное число (например: 85.50)");
            }
        }
    }

    public static BigDecimal readOptionalBigDecimal(Scanner scanner, String prompt, BigDecimal min, BigDecimal max) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim().replace(',', '.');

                if (input.isEmpty()) {
                    return null;
                }

                BigDecimal value = new BigDecimal(input);

                if (value.compareTo(min) < 0) {
                    System.out.printf("Ошибка: Цена не может быть меньше %.2f\n", min);
                    continue;
                }

                if (value.compareTo(max) > 0) {
                    System.out.printf("Ошибка: Цена не может быть больше %.2f\n", max);
                    continue;
                }

                return value;

            } catch (NumberFormatException e) {
                System.out.println("Ошибка: Введите корректное число (например: 85.50)");
            }
        }
    }

    public static LocalDate readDate(Scanner scanner, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();

                if (input.isEmpty()) {
                    System.out.println("Ошибка: Ввод не может быть пустым");
                    continue;
                }

                LocalDate date = LocalDate.parse(input, DATE_FORMATTER);
                LocalDate today = LocalDate.now();

                if (date.isBefore(today)) {
                    System.out.println("Ошибка: Дата не может быть в прошлом");
                    continue;
                }

                if (date.isAfter(today.plusYears(10))) {
                    System.out.println("Ошибка: Дата слишком далеко в будущем");
                    continue;
                }

                return date;

            } catch (DateTimeParseException e) {
                System.out.println("Ошибка: Введите дату в формате ГГГГ-ММ-ДД (например: 2024-12-31)");
            }
        }
    }

    public static LocalDate readOptionalDate(Scanner scanner, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();

                if (input.isEmpty()) {
                    return null;
                }

                LocalDate date = LocalDate.parse(input, DATE_FORMATTER);
                LocalDate today = LocalDate.now();

                if (date.isBefore(today)) {
                    System.out.println("Ошибка: Дата не может быть в прошлом");
                    continue;
                }

                return date;

            } catch (DateTimeParseException e) {
                System.out.println("Ошибка: Введите дату в формате ГГГГ-ММ-ДД (например: 2024-12-31)");
            }
        }
    }

    public static String readString(Scanner scanner, String prompt, int minLength, int maxLength) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Ошибка: Ввод не может быть пустым");
                continue;
            }

            if (input.length() < minLength) {
                System.out.printf("Ошибка: Минимальная длина %d символов\n", minLength);
                continue;
            }

            if (input.length() > maxLength) {
                System.out.printf("Ошибка: Максимальная длина %d символов\n", maxLength);
                continue;
            }

            return input;
        }
    }

    public static String readOptionalString(Scanner scanner, String prompt, int minLength, int maxLength) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                return "";
            }

            if (input.length() < minLength) {
                System.out.printf("Ошибка: Минимальная длина %d символов\n", minLength);
                continue;
            }

            if (input.length() > maxLength) {
                System.out.printf("Ошибка: Максимальная длина %d символов\n", maxLength);
                continue;
            }

            return input;
        }
    }

    public static int readMenuChoice(Scanner scanner, int minChoice, int maxChoice) {
        while (true) {
            System.out.print("Выберите действие: ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Ошибка: Введите номер пункта меню");
                continue;
            }

            try {
                int choice = Integer.parseInt(input);

                if (choice >= minChoice && choice <= maxChoice) {
                    return choice;
                } else {
                    System.out.printf("Ошибка: Введите число от %d до %d\n", minChoice, maxChoice);
                }

            } catch (NumberFormatException e) {
                System.out.println("Ошибка: Введите корректный номер пункта");
            }
        }
    }

    public static boolean readConfirmation(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("да") || input.equals("д") || input.equals("y") || input.equals("yes")) {
                return true;
            } else if (input.equals("нет") || input.equals("н") || input.equals("n") || input.equals("no")) {
                return false;
            } else {
                System.out.println("Ошибка: Введите 'да' или 'нет'");
            }
        }
    }
}