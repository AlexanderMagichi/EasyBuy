package com.teamchallenge.easybuy;
import com.teamchallenge.easybuy.infrastructure.exception.handler.ApiErrorResponseCreator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@IntegrationTest
@MockitoBean(types = org.springframework.data.redis.connection.RedisConnectionFactory.class)
class EasyBuyApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        assertNotNull(applicationContext);
    }

}
