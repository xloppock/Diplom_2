package tests;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import static org.hamcrest.Matchers.*;
import static org.apache.http.HttpStatus.*;
import clients.UserApi;
import models.User;

public class UserCreationTest {

    private UserApi userApi = new UserApi();
    private User createdUser;

    @Test
    @DisplayName("Создание уникального пользователя")
    public void createUniqueUserShouldBeSuccessful() {
        User uniqueUser = userApi.createUniqueTestUser();

        Response response = userApi.register(uniqueUser);

        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("user.email", equalTo(uniqueUser.getEmail()))
                .body("user.name", equalTo(uniqueUser.getName()))
                .body("accessToken", startsWith("Bearer"))
                .body("refreshToken", notNullValue());

        userApi.parseTokensFromResponse(response, uniqueUser);
        createdUser = uniqueUser;
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    public void createExistingUserShouldReturn403() {
        User existingUser = userApi.createUniqueTestUser();

        Response firstRegister = userApi.register(existingUser);
        firstRegister.then().statusCode(SC_OK);
        userApi.parseTokensFromResponse(firstRegister, existingUser);

        Response secondRegister = userApi.register(existingUser);

        secondRegister.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));

        userApi.deleteUser(existingUser.getAccessToken());
    }

    @Test
    @DisplayName("Создание пользователя без email")
    public void createUserWithoutEmailShouldReturn403() {
        User userWithoutEmail = new User(
                null, // нет email
                "Test User",
                "password123"
        );

        Response response = userApi.register(userWithoutEmail);

        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без пароля")
    public void createUserWithoutPasswordShouldReturn403() {
        User userWithoutPassword = new User(
                "test@mail.com",
                "Test User",
                null // нет пароля
        );

        Response response = userApi.register(userWithoutPassword);

        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без имени")
    public void createUserWithoutNameShouldReturn403() {
        // Arrange
        User userWithoutName = new User(
                "test@mail.com",
                null, // нет имени
                "password123"
        );

        Response response = userApi.register(userWithoutName);

        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @After
    public void tearDown() {
        if (createdUser != null && createdUser.getAccessToken() != null) {
            userApi.deleteUser(createdUser.getAccessToken());
        }
    }
}