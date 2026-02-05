package tests;

import clients.OrderApi;
import clients.UserApi;
import clients.UserCreation;
import constants.Constants;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import static org.hamcrest.Matchers.*;
import static org.apache.http.HttpStatus.*;
import io.qameta.allure.Description;

public class OrderCreationTest {

    private UserApi userApi = new UserApi();
    private OrderApi orderApi = new OrderApi();
    private  UserCreation userCreation = new UserCreation();
    private User testUser;

    @Before
    public void setUp() {
        testUser = userCreation.createUniqueTestUser();
        Response registerResponse = userApi.register(testUser);
        registerResponse.then().statusCode(SC_OK);
        userApi.parseTokensFromResponse(registerResponse, testUser);
    }

    @After
    public void tearDown() {
        if (testUser != null && testUser.getAccessToken() != null) {
            userApi.deleteUser(testUser.getAccessToken());
        }
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и валидными ингредиентами")
    @Description("Позитивный тест: проверка успешного создания заказа авторизованным пользователем с валидными ингредиентами. Ожидается статус 200, success: true и присвоенный номер заказа.")
    public void createOrderWithAuthAndValidIngredientsShouldBeSuccessful() {
        List<String> ingredients = Arrays.asList(
                Constants.VALID_INGREDIENTS[0],
                Constants.VALID_INGREDIENTS[2]
        );

        Response response = orderApi.createOrder(ingredients, testUser.getAccessToken());

        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());

        int orderNumber = response.jsonPath().getInt("order.number");
        System.out.println("Создан заказ №" + orderNumber);
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description("Негативный тест: попытка создания заказа без токена авторизации. Ожидается статус 401 с сообщением 'You should be authorised'.")
    public void createOrderWithoutAuthShouldReturn401() {
        List<String> ingredients = Arrays.asList(
                Constants.VALID_INGREDIENTS[0],
                Constants.VALID_INGREDIENTS[2]
        );

        Response response = orderApi.createOrder(ingredients, null);

        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Создание заказа с ингредиентами")
    @Description("Позитивный тест: создание заказа с несколькими (4) валидными ингредиентами, включая повторяющийся. Проверка успешного ответа и наличия номера заказа.")
    public void createOrderWithIngredientsShouldBeSuccessful() {
        List<String> ingredients = Arrays.asList(
                Constants.VALID_INGREDIENTS[0],
                Constants.VALID_INGREDIENTS[2],
                Constants.VALID_INGREDIENTS[4],
                Constants.VALID_INGREDIENTS[0]
        );

        Response response = orderApi.createOrder(ingredients, testUser.getAccessToken());

        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", greaterThan(0));
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    @Description("Негативный тест: создание заказа с пустым списком ингредиентов. Ожидается статус 400 с сообщением 'Ingredient ids must be provided'.")
    public void createOrderWithoutIngredientsShouldReturn400() {
        List<String> ingredients = new ArrayList<>();

        Response response = orderApi.createOrder(ingredients, testUser.getAccessToken());

        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с null ингредиентами")
    @Description("Негативный тест: отправка запроса с пустым JSON телом (ingredients: null). Ожидается статус 400 с сообщением об обязательности ингредиентов.")
    public void createOrderWithNullIngredientsShouldReturn400() {
        Response response = orderApi.createWithNullIngredients(testUser.getAccessToken()
        );
        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    @Description("Негативный тест: использование несуществующих хешей ингредиентов. Ожидается статус 500 - внутренняя ошибка сервера.")
    public void createOrderWithInvalidIngredientHashShouldReturn500() {
        List<String> invalidIngredients = Arrays.asList(
                "invalid_hash_1",
                "invalid_hash_2"
        );

        Response response = orderApi.createOrder(invalidIngredients, testUser.getAccessToken());

        response.then()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Создание заказа с одним неверным хешем среди валидных")
    @Description("Негативный тест: смешивание валидных и невалидных хешей ингредиентов в одном заказе. Ожидается статус 500.")
    public void createOrderWithMixedIngredientHashesShouldReturn500() {
        List<String> mixedIngredients = Arrays.asList(
                Constants.VALID_INGREDIENTS[0],
                "invalid_hash_123",
                Constants.VALID_INGREDIENTS[1]
        );

        Response response = orderApi.createOrder(mixedIngredients, testUser.getAccessToken());

        response.then()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Проверка доступности ингредиентов")
    @Description("Позитивный тест: получение списка всех доступных ингредиентов. Проверка успешного ответа и наличия данных.")
    public void getIngredientsShouldReturnValidData() {
        Response response = orderApi.getIngredients();

        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("data", notNullValue())
                .body("data.size()", greaterThan(0));
    }
}