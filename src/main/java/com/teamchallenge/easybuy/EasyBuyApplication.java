package com.teamchallenge.easybuy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.retry.annotation.EnableRetry;

import java.util.TimeZone;

@EnableRetry
@EnableJpaRepositories(basePackages = {
        "com.teamchallenge.easybuy.shop.repository",
        "com.teamchallenge.easybuy.product.repository",
        "com.teamchallenge.easybuy.user.repository",
        "com.teamchallenge.easybuy.auth.repository"
})
@Configuration
@SpringBootApplication
public class EasyBuyApplication {

    static {
        // Avoid invalid legacy timezone ids (for example Europe/Kiev) in JDBC startup params.
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    public static void main(String[] args) {
        SpringApplication.run(EasyBuyApplication.class, args);
    }

}
