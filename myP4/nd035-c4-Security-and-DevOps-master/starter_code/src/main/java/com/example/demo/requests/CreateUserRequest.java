package com.example.demo.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
public class CreateUserRequest {

    @JsonProperty
    private String username;
    @JsonProperty
    private String password;

    public CreateUserRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
