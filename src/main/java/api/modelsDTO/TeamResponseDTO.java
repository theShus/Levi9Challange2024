package api.modelsDTO;

import java.util.List;
import java.util.UUID;

public class TeamResponseDTO {

    private UUID id;
    private String teamName;
    private List<PlayerResponseDTO> players;

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

    public List<PlayerResponseDTO> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerResponseDTO> players) {
        this.players = players;
    }
}
