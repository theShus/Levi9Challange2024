package api.repositories;

import api.models.Match;
import api.models.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MatchRepositoryI extends JpaRepository<Match, UUID> {

    List<Match> findByTeam1OrTeam2(Team team1, Team team2);
}
