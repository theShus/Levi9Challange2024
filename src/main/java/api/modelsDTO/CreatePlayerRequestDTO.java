package api.modelsDTO;

import jakarta.validation.constraints.NotBlank;

public class CreatePlayerRequestDTO {

    @NotBlank(message = "Nickname is mandatory")
    private String nickname;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}