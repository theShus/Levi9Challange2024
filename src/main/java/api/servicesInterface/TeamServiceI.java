package api.servicesInterface;


import api.modelsDTO.CreateTeamRequestDTO;
import api.modelsDTO.SwapPlayersRequestDTO;
import api.modelsDTO.TeamResponseDTO;

import java.util.UUID;

public interface TeamServiceI {

    TeamResponseDTO createTeam(CreateTeamRequestDTO request);

    TeamResponseDTO getTeamById(UUID teamId);

    void deleteTeam(UUID teamId);

    void swapPlayers(SwapPlayersRequestDTO request);
}
