package api.models;

import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "teams")
public class Team {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true, nullable = false)
    private String teamName;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private List<Player> players;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }
}
