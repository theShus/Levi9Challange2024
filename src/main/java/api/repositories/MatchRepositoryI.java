package api.repositories;

import api.models.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MatchRepositoryI extends JpaRepository<Match, UUID> {
    // Additional query methods if needed
}
