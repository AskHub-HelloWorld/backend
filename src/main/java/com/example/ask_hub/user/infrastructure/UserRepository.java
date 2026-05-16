package com.example.ask_hub.user.infrastructure;

import com.example.ask_hub.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(Long userId);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);


    List<User> findAllByIdIn(Collection<Long> ids);

    List<User> findAllByCompany(String company);
}
