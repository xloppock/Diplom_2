package clients;

import models.*;
import constants.Constants;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import static io.restassured.RestAssured.given;

public class OrderApi {

    @Step("Создание заказа")
    public Response createOrder(List<String> ingredients, String accessToken) {
        Order order = new Order(ingredients);

        if (accessToken != null && !accessToken.isEmpty()) {
            return given()
                    .header("Content-type", "application/json")
                    .header("Authorization", accessToken)
                    .body(order)
                    .when()
                    .post(Constants.ORDERS_ENDPOINT);
        } else {
            return given()
                    .header("Content-type", "application/json")
                    .body(order)
                    .when()
                    .post(Constants.ORDERS_ENDPOINT);
        }
    }

    @Step("Получение списка ингредиентов")
    public Response getIngredients() {
        return given()
                .when()
                .get(Constants.INGREDIENTS_ENDPOINT);
    }
}