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


public class UserApi {

    @Step("Регистрация нового пользователя")
    public Response register(User user) {
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post(Constants.REGISTER_ENDPOINT);
    }

    @Step("Авторизация пользователя")
    public Response login(User user) {
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post(Constants.LOGIN_ENDPOINT);
    }

    @Step("Удаление пользователя")
    public Response deleteUser(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .when()
                .delete(Constants.USER_ENDPOINT);
    }

    @Step("Получение данных пользователя")
    public Response getUser(String accessToken) {
        return given()
                .header("Authorization", accessToken)
                .when()
                .get(Constants.USER_ENDPOINT);
    }

    @Step("Создание уникального тестового пользователя")
    public User createUniqueTestUser() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        return new User(
                "testuser_" + timestamp + "@mail.com",
                "Test models.User " + timestamp,
                "password123"
        );
    }

    @Step("Парсинг токенов из ответа")
    public void parseTokensFromResponse(Response response, User user) {
        if (response.statusCode() == 200) {
            user.setAccessToken(response.jsonPath().getString("accessToken"));
            user.setRefreshToken(response.jsonPath().getString("refreshToken"));
        }
    }
}