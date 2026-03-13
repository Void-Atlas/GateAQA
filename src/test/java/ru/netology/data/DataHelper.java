package ru.netology.data;

import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DataHelper {

    private static Faker faker = new Faker(new Locale("en"));

    public static String approvedCard() {
        return "4444 4444 4444 4441";
    }

    public static String declinedCard() {
        return "4444 4444 4444 4442";
    }

    public static String generateMonth() {
        return LocalDate.now()
                .plusMonths(faker.number().numberBetween(1, 12))
                .format(DateTimeFormatter.ofPattern("MM"));
    }

    public static String generateYear() {
        return LocalDate.now()
                .plusYears(faker.number().numberBetween(1, 4))
                .format(DateTimeFormatter.ofPattern("yy"));
    }

    public static String generateOwner() {
        return faker.name().firstName().toUpperCase() + " " +
                faker.name().lastName().toUpperCase();
    }

    public static String generateCVC() {
        return faker.number().digits(3);
    }
}