package api.modelsDTO;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public class SwapPlayersRequestDTO {

    @NotNull(message = "Team1 ID cannot be null")
    private UUID team1Id;

    @NotNull(message = "Team2 ID cannot be null")
    private UUID team2Id;

    @NotEmpty(message = "Team1 player IDs cannot be empty")
    private List<UUID> team1PlayerIds;

    @NotEmpty(message = "Team2 player IDs cannot be empty")
    private List<UUID> team2PlayerIds;

    public UUID getTeam1Id() {
        return team1Id;
    }

    public void setTeam1Id(UUID team1Id) {
        this.team1Id = team1Id;
    }

    public UUID getTeam2Id() {
        return team2Id;
    }

    public void setTeam2Id(UUID team2Id) {
        this.team2Id = team2Id;
    }

    public List<UUID> getTeam1PlayerIds() {
        return team1PlayerIds;
    }

    public void setTeam1PlayerIds(List<UUID> team1PlayerIds) {
        this.team1PlayerIds = team1PlayerIds;
    }

    public List<UUID> getTeam2PlayerIds() {
        return team2PlayerIds;
    }

    public void setTeam2PlayerIds(List<UUID> team2PlayerIds) {
        this.team2PlayerIds = team2PlayerIds;
    }
}