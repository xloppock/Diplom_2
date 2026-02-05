package tests;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import static org.hamcrest.Matchers.*;
import static org.apache.http.HttpStatus.*;
import clients.UserApi;
import clients.UserCreation;
import models.User;
import io.qameta.allure.Description;

public class UserCreationTest {

    private UserApi userApi = new UserApi();
    private UserCreation userCreation = new UserCreation();
    private User createdUser;
    private Response createUserResponse;

    @After
    public void tearDown() {
        if (createdUser != null && createUserResponse != null) {
            userApi.parseTokensFromResponse(createUserResponse, createdUser);

            if (createdUser.getAccessToken() != null) {
                userApi.deleteUser(createdUser.getAccessToken());
            }
        }
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    @Description("Позитивный тест: успешная регистрация нового пользователя с уникальными данными. Проверка корректности ответа и наличия токенов.")
    public void createUniqueUserShouldBeSuccessful() {
        User uniqueUser = userCreation.createUniqueTestUser();

        createUserResponse = userApi.register(uniqueUser);
        createdUser = uniqueUser;

        createUserResponse.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("user.email", equalTo(uniqueUser.getEmail()))
                .body("user.name", equalTo(uniqueUser.getName()))
                .body("accessToken", startsWith("Bearer"))
                .body("refreshToken", notNullValue());
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    @Description("Негативный тест: попытка повторной регистрации пользователя с теми же данными. Ожидается статус 403 с сообщением 'User already exists'.")
    public void createExistingUserShouldReturn403() {
        User existingUser = userCreation.createUniqueTestUser();

        Response firstRegister = userApi.register(existingUser);

        createdUser = existingUser;
        createUserResponse = firstRegister;

        firstRegister.then().statusCode(SC_OK);

        Response secondRegister = userApi.register(existingUser);

        secondRegister.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));

    }

    @Test
    @DisplayName("Создание пользователя без email")
    @Description("Негативный тест: регистрация пользователя без указания email. Ожидается статус 403 с сообщением о обязательных полях.")
    public void createUserWithoutEmailShouldReturn403() {
        User userWithoutEmail = new User(
                null, // нет email
                "Test User",
                "password123"
        );

        Response response = userApi.register(userWithoutEmail);

        createdUser = userWithoutEmail;
        createUserResponse = response;

        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без пароля")
    @Description("Негативный тест: регистрация пользователя без указания пароля. Ожидается статус 403 с сообщением о обязательных полях.")
    public void createUserWithoutPasswordShouldReturn403() {
        User userWithoutPassword = new User(
                "test@mail.com",
                "Test User",
                null // нет пароля
        );

        Response response = userApi.register(userWithoutPassword);

        createdUser = userWithoutPassword;
        createUserResponse = response;


        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без имени")
    @Description("Негативный тест: регистрация пользователя без указания имени. Ожидается статус 403 с сообщением о обязательных полях.")
    public void createUserWithoutNameShouldReturn403() {
        User userWithoutName = new User(
                "test@mail.com",
                null, // нет имени
                "password123"
        );

        Response response = userApi.register(userWithoutName);

        createdUser = userWithoutName;
        createUserResponse = response;

                response.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}