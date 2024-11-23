package api.modelsDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public class CreateTeamRequestDTO {

    @NotBlank(message = "Team name is mandatory")
    private String teamName;

    //@Size(min = 5, max = 5, message = "Team must have exactly 5 players")
    private List<UUID> players;

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public List<UUID> getPlayers() {
        return players;
    }

    public void setPlayers(List<UUID> players) {
        this.players = players;
    }
}
