package constants;

public class Constants {
    public static final String BASE_URL = "https://stellarburgers.education-services.ru/api";

    // Auth endpoints
    public static final String REGISTER_ENDPOINT = BASE_URL + "/auth/register";
    public static final String LOGIN_ENDPOINT = BASE_URL + "/auth/login";
    public static final String USER_ENDPOINT = BASE_URL + "/auth/user";

    // models.Order endpoints
    public static final String ORDERS_ENDPOINT = BASE_URL + "/orders";
    public static final String ORDERS_ALL_ENDPOINT = BASE_URL + "/orders/all";

    // Ingredients endpoint
    public static final String INGREDIENTS_ENDPOINT = BASE_URL + "/ingredients";

    // Test ingredients (валидные хеши из документации)
    public static final String[] VALID_INGREDIENTS = {
            "61c0c5a71d1f82001bdaaa6c",
            "61c0c5a71d1f82001bdaaa6d",
            "61c0c5a71d1f82001bdaaa72",
            "61c0c5a71d1f82001bdaaa73",
            "61c0c5a71d1f82001bdaaa74",
            "61c0c5a71d1f82001bdaaa75",
            "61c0c5a71d1f82001bdaaa6f",
            "61c0c5a71d1f82001bdaaa70",
            "61c0c5a71d1f82001bdaaa71",
            "61c0c5a71d1f82001bdaaa6e",
            "61c0c5a71d1f82001bdaaa76",
            "61c0c5a71d1f82001bdaaa77",
            "61c0c5a71d1f82001bdaaa78",
            "61c0c5a71d1f82001bdaaa79",
            "61c0c5a71d1f82001bdaaa7a"
    };
}