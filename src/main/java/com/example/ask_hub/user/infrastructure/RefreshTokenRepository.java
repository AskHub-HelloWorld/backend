package com.example.ask_hub.user.infrastructure;

import com.example.ask_hub.user.domain.entity.RefreshToken;
import com.example.ask_hub.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByUser(User user);

}
