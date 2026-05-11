package com.example.ask_hub.team.infrastructure;

import com.example.ask_hub.team.domain.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
