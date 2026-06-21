package generators;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.concurrent.ThreadLocalRandom;

public class RandomData {

    private RandomData() {
    }

    public static String getUsername() {
        return RandomStringUtils.randomAlphabetic(10).toLowerCase();
    }

    public static String getPassword() {
        return RandomStringUtils.randomAlphabetic(3).toUpperCase()
                + RandomStringUtils.randomAlphabetic(5).toLowerCase()
                + RandomStringUtils.randomNumeric(3)
                + "$";
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    public static String getFullName() {
        String firstName = RandomStringUtils.randomAlphabetic(6, 10);
        String lastName = RandomStringUtils.randomAlphabetic(6, 12);

        return capitalize(firstName) + " " +  capitalize(lastName);
    }

    public static double getDouble(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    public static double getDepositAmount() {
        double amount = getDouble(0.01, 5000.01);
        return Math.round(amount * 100.0) / 100.0;
    }

    public static double getTransferAmount() {
        double amount = getDouble(0.01, 5000.0);
        return Math.round(amount * 100.0) / 100.0;
    }
}
