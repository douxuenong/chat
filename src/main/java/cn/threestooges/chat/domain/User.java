package cn.threestooges.chat.domain;

import lombok.Data;

@Data
public class User {

    private Long id;

    private String username;

    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
