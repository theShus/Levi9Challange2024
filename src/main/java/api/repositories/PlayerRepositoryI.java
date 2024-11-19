package api.repositories;

import api.models.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface PlayerRepositoryI extends JpaRepository<Player, UUID> {

    Optional<Player> findByNickname(String nickname);

    boolean existsByNickname(String nickname);
}
