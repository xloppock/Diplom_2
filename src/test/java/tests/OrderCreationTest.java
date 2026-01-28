package tests;

import clients.OrderApi;
import clients.UserApi;
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
import static io.restassured.RestAssured.given;

public class OrderCreationTest {

    private UserApi userApi = new UserApi();
    private OrderApi orderApi = new OrderApi();
    private User testUser;

    @Before
    public void setUp() {
        testUser = userApi.createUniqueTestUser();
        Response registerResponse = userApi.register(testUser);
        registerResponse.then().statusCode(SC_OK);
        userApi.parseTokensFromResponse(registerResponse, testUser);
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и валидными ингредиентами")
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
    public void createOrderWithNullIngredientsShouldReturn400() {
        Response response = given()
                .header("Content-type", "application/json")
                .header("Authorization", testUser.getAccessToken())
                .body("{}")
                .when()
                .post(Constants.ORDERS_ENDPOINT);

        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
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
    public void getIngredientsShouldReturnValidData() {
        Response response = orderApi.getIngredients();

        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("data", notNullValue())
                .body("data.size()", greaterThan(0));
    }

    @After
    public void tearDown() {
        if (testUser != null && testUser.getAccessToken() != null) {
            userApi.deleteUser(testUser.getAccessToken());
        }
    }
}