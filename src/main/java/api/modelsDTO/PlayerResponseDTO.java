package api.modelsDTO;

import java.util.UUID;

public class PlayerResponseDTO {

    private UUID id;
    private String nickname;
    private int wins;
    private int losses;
    private int elo;
    private int hoursPlayed;
    private UUID teamId;
    private Integer ratingAdjustment;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getElo() {
        return elo;
    }

    public void setElo(int elo) {
        this.elo = elo;
    }

    public int getHoursPlayed() {
        return hoursPlayed;
    }

    public void setHoursPlayed(int hoursPlayed) {
        this.hoursPlayed = hoursPlayed;
    }

    public UUID getTeamId() {
        return teamId;
    }

    public void setTeamId(UUID teamId) {
        this.teamId = teamId;
    }

    public Integer getRatingAdjustment() {
        return ratingAdjustment;
    }

    public void setRatingAdjustment(Integer ratingAdjustment) {
        this.ratingAdjustment = ratingAdjustment;
    }
}
