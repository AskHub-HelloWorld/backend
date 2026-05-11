package com.example.ask_hub.team.infrastructure;

import com.example.ask_hub.team.domain.entity.Convention;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConventionRepository extends JpaRepository<Convention, Long> {
    List<Convention> findAllByTeamId(Long teamId);
}
