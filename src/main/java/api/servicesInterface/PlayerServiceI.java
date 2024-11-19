package api.servicesInterface;


import api.modelsDTO.CreatePlayerRequestDTO;
import api.modelsDTO.PlayerResponseDTO;

import java.util.List;
import java.util.UUID;

public interface PlayerServiceI {

    List<PlayerResponseDTO> getAllPlayers();

    PlayerResponseDTO createPlayer(CreatePlayerRequestDTO request);

    PlayerResponseDTO getPlayerById(UUID playerId);
}
