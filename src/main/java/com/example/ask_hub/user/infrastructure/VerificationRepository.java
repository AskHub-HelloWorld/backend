package com.example.ask_hub.user.infrastructure;

import com.example.ask_hub.user.domain.entity.Verification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationRepository extends JpaRepository<Verification, Long> {


}
