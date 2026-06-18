package com.teamchallenge.easybuy.auth.repository;

import com.teamchallenge.easybuy.auth.entity.Token;
import com.teamchallenge.easybuy.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {
    Optional<Token> findByToken(String token);

    void deleteAllByUser(UserEntity user);

    List<Token> findAllByUserAndRevokedFalse(UserEntity user);
}
