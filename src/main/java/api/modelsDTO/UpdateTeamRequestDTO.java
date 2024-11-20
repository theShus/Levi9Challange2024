package api.modelsDTO;

import jakarta.validation.constraints.NotBlank;

public class UpdateTeamRequestDTO {

    @NotBlank(message = "Team name cannot be blank")
    private String teamName;

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
}
