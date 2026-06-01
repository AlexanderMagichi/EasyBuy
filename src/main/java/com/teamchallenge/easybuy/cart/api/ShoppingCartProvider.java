package com.teamchallenge.easybuy.cart.api;

import com.teamchallenge.easybuy.cart.converter.ShoppingCartDtoConverter;
import com.teamchallenge.easybuy.cart.entity.ShoppingCart;
import com.teamchallenge.easybuy.cart.exception.ShoppingCartNotFoundException;
import com.teamchallenge.easybuy.cart.repository.ShoppingCartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShoppingCartProvider {

    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartDtoConverter shoppingCartDtoConverter;
    private final ShoppingCartCreator shoppingCartCreator;

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public ShoppingCartDto getByUserId(final UUID userId) {
        ShoppingCart shoppingCart = shoppingCartCreator.getOrCreate(userId);
        return shoppingCartDtoConverter.toDto(shoppingCart);
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, readOnly = true)
    public ShoppingCartDto getByUserIdOrThrow(final UUID userId) {
        return shoppingCartRepository.findShoppingCartByUserId(userId)
                .map(shoppingCartDtoConverter::toDto)
                .orElseThrow(() -> new ShoppingCartNotFoundException(userId));
    }
}