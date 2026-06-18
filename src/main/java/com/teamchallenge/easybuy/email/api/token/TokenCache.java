package com.teamchallenge.easybuy.email.api.token;


import com.teamchallenge.easybuy.openapi.dto.UserRegistrationRequest;

public interface TokenCache {

    void addToken(String tokenKey,
                  UserRegistrationRequest request);

    UserRegistrationRequest getToken(String tokenKey);

    void removeToken(String tokenKey);
}
