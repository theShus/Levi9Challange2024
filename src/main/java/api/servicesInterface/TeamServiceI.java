package api.servicesInterface;


import api.modelsDTO.CreateTeamRequestDTO;
import api.modelsDTO.TeamResponseDTO;

import java.util.UUID;

public interface TeamServiceI {

    TeamResponseDTO createTeam(CreateTeamRequestDTO request);

    TeamResponseDTO getTeamById(UUID teamId);
}
