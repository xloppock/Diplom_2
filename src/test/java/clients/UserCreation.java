package clients;

import constants.Constants;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.User;
import java.util.HashMap;
import java.util.Map;

public class UserCreation {

    @Step("Создание уникального тестового пользователя")
    public User createUniqueTestUser() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        return new User(
                "testuser_" + timestamp + "@mail.com",
                "Test models.User " + timestamp,
                "password123"
        );
    }
}
