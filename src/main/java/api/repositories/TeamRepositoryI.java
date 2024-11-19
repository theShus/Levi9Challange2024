package api.repositories;

import api.models.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TeamRepositoryI extends JpaRepository<Team, UUID> {

    boolean existsByTeamName(String teamName);

    Optional<Team> findByTeamName(String teamName);
}
