package com.teamchallenge.easybuy.order.api;


import com.teamchallenge.easybuy.order.entity.Order;
import com.teamchallenge.easybuy.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.teamchallenge.easybuy.openapi.dto.OrderDto;
import com.teamchallenge.easybuy.openapi.dto.OrderStatus;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderProvider {

    private final OrderRepository orderRepository;

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, readOnly = true)
    public Optional<Order> getOrderEntityByUserAndSession(final UUID userId, final String sessionId) {
        return orderRepository.findByUserIdAndSessionId(userId, sessionId);
    }
}
