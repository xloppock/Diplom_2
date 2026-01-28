package tests;

import clients.UserApi;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.Matchers.*;
import static org.apache.http.HttpStatus.*;

public class UserLoginTest {

    private UserApi userApi = new UserApi();
    private User testUser;

    @Before
    public void setUp() {
        testUser = userApi.createUniqueTestUser();
        Response registerResponse = userApi.register(testUser);
        registerResponse.then().statusCode(SC_OK);
        userApi.parseTokensFromResponse(registerResponse, testUser);
    }

    @Test
    @DisplayName("Вход под существующим пользователем")
    public void loginWithExistingUserShouldBeSuccessful() {
        Response loginResponse = userApi.login(testUser);

        loginResponse.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("accessToken", startsWith("Bearer"))
                .body("refreshToken", notNullValue())
                .body("user.email", equalTo(testUser.getEmail()))
                .body("user.name", equalTo(testUser.getName()));
    }

    @Test
    @DisplayName("Вход с неверным email")
    public void loginWithWrongEmailShouldReturn401() {
        User wrongUser = new User(
                "wrong_" + System.currentTimeMillis() + "@mail.com", // неверный email
                testUser.getName(),
                testUser.getPassword()
        );

        Response loginResponse = userApi.login(wrongUser);

        loginResponse.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Вход с неверным паролем")
    public void loginWithWrongPasswordShouldReturn401() {
        User wrongUser = new User(
                testUser.getEmail(),
                testUser.getName(),
                "wrong_password_" + System.currentTimeMillis() // неверный пароль
        );

        Response loginResponse = userApi.login(wrongUser);

        loginResponse.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Вход без email")
    public void loginWithoutEmailShouldReturn401() {
        User userWithoutEmail = new User(
                null,
                testUser.getName(),
                testUser.getPassword()
        );

        Response loginResponse = userApi.login(userWithoutEmail);

        loginResponse.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Вход без пароля")
    public void loginWithoutPasswordShouldReturn401() {
        User userWithoutPassword = new User(
                testUser.getEmail(),
                testUser.getName(),
                null
        );

        Response loginResponse = userApi.login(userWithoutPassword);

        loginResponse.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @After
    public void tearDown() {
        if (testUser != null && testUser.getAccessToken() != null) {
            userApi.deleteUser(testUser.getAccessToken());
        }
    }
}