package api.modelsDTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class CreateMatchRequestDTO {

    @NotNull(message = "Team1Id is mandatory")
    private UUID team1Id;

    @NotNull(message = "Team2Id is mandatory")
    private UUID team2Id;

    private UUID winningTeamId; // Can be null for a draw

    @Min(value = 1, message = "Duration must be at least 1")
    private int duration;

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

    public UUID getWinningTeamId() {
        return winningTeamId;
    }

    public void setWinningTeamId(UUID winningTeamId) {
        this.winningTeamId = winningTeamId;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
