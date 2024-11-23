package api.modelsDTO;

import api.models.Player;

import java.util.Set;

public class GeneratedTeamsDto {
    Set<Player> team1;
    Set<Player> team2;

    public Set<Player> getTeam2() {
        return team2;
    }

    public void setTeam2(Set<Player> team2) {
        this.team2 = team2;
    }

    public Set<Player> getTeam1() {
        return team1;
    }

    public void setTeam1(Set<Player> team1) {
        this.team1 = team1;
    }
}
