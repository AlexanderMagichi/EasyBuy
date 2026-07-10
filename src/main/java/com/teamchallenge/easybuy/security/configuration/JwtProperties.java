package com.teamchallenge.easybuy.security.configuration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Getter
@Setter
@Validated
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    @NotBlank(message = "JWT header cannot be blank")
    private String header;

    @NotBlank(message = "JWT secret cannot be blank")
    private String secret;

    @NotBlank(message = "JWT refresh secret cannot be blank")
    private String refreshSecret;

    @NotNull(message = "JWT expiration cannot be null")
    private Duration expiration;

    @NotNull(message = "JWT refresh expiration cannot be null")
    private Duration refreshExpiration;

    @NotBlank(message = "JWT issuer cannot be blank")
    private String issuer;

    @NotBlank(message = "JWT audience cannot be blank")
    private String audience;
}
