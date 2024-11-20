package api.modelsDTO;

import java.util.UUID;

public class MatchResponseDTO {

    private UUID id;
    private UUID team1Id;
    private UUID team2Id;
    private UUID winningTeamId;
    private Integer duration;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }
}
