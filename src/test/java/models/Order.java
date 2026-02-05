package models;

import java.util.List;

public class Order {
    private String number;
    private String name;
    private List<String> ingredients;

    public Order() {}

    public Order(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    // Геттеры и сеттеры
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<String> getIngredients() { return ingredients; }
    public void setIngredients(List<String> ingredients) { this.ingredients = ingredients; }
}