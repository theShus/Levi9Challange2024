package api.modelsDTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class UpdatePlayerRequestDTO {

    @NotBlank(message = "Nickname cannot be blank")
    private String nickname;

    @Min(value = 0, message = "Wins cannot be negative")
    private int wins;

    @Min(value = 0, message = "Losses cannot be negative")
    private int losses;

    @Min(value = 0, message = "Elo cannot be negative")
    private int elo;

    @Min(value = 0, message = "Hours played cannot be negative")
    private int hoursPlayed;

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
}
