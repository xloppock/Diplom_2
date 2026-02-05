package models;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String email;
    private String name;
    private String password;
    private String accessToken;
    private String refreshToken;

    public User(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.accessToken = null;
        this.refreshToken = null;
    }
}