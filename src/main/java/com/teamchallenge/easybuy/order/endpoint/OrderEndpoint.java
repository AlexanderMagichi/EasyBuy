package com.teamchallenge.easybuy.order.endpoint;


import com.teamchallenge.easybuy.openapi.dto.OrderStatus;
import com.teamchallenge.easybuy.order.api.OrderCreator;
import com.teamchallenge.easybuy.order.api.OrdersProvider;

import com.teamchallenge.easybuy.security.api.SecurityPrincipalProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(value = OrderEndpoint.ORDERS_URL)
public class OrderEndpoint {

    public static final String ORDERS_URL = "/api/v1/orders";

    private final SecurityPrincipalProvider securityPrincipalProvider;
    private final OrdersProvider ordersProvider;
    private final OrderCreator orderCreator;

    @GetMapping
    public ResponseEntity<List<SpringDataJaxb.OrderDto>> getOrders(
            @RequestParam(required = false) final List<OrderStatus> status) {
        var userId = securityPrincipalProvider.getUserId();
        log.info("orders.get: userId={}", userId);
        var orders = ordersProvider.getOrders(userId, status);
        log.info("orders.retrieved: userId={}, count={}", userId, orders.size());
        return ResponseEntity.ok(orders);
    }

    @PostMapping
    public ResponseEntity<SpringDataJaxb.OrderDto> createOrder(@Valid @RequestBody final CreateNewOrderRequestDto request) {
        var userId = securityPrincipalProvider.getUserId();
        log.info("orders.create: userId={}", userId);
        var order = orderCreator.create(userId, request);
        log.info("orders.created: userId={}, orderId={}", userId, order.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }
}
