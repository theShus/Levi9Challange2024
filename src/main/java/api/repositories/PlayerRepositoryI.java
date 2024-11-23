package api.repositories;

import api.models.Player;
import api.models.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface PlayerRepositoryI extends JpaRepository<Player, UUID> {

    Optional<Player> findByNickname(String nickname);

    boolean existsByNickname(String nickname);

    @Query("select p from Player p where p.team is null order by p.elo desc")
    Set<Player> findAllForGeneratedTeam();
}
