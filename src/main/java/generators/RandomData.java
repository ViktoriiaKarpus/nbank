package generators;

import org.apache.commons.lang3.RandomStringUtils;

public class RandomData {

    private RandomData(){}

    public static String getUserName(){
        return RandomStringUtils.randomAlphabetic(10).toLowerCase();
    }

    public static String getPassword(){
        return RandomStringUtils.randomAlphabetic(3).toUpperCase()
                + RandomStringUtils.randomAlphabetic(5).toUpperCase()
                + "$";
    }

    public static String getFullName(){
        return "John Smith";
    }
}
