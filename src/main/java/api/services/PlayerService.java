package api.services;

import api.modelsDTO.CreatePlayerRequestDTO;
import api.modelsDTO.PlayerResponseDTO;
import api.models.Player;
import api.repositories.PlayerRepositoryI;
import api.servicesInterface.PlayerServiceI;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import api.exceptions.DuplicateResourceException;
import api.exceptions.InvalidInputException;
import api.exceptions.ResourceNotFoundException;

@Service
public class PlayerService implements PlayerServiceI {

    private final PlayerRepositoryI playerRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public PlayerService(PlayerRepositoryI playerRepository, ModelMapper modelMapper) {
        this.playerRepository = playerRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    public PlayerResponseDTO createPlayer(CreatePlayerRequestDTO request) {
        if (playerRepository.existsByNickname(request.getNickname())) {
            throw new DuplicateResourceException("Nickname already exists");
        }

        Player player = modelMapper.map(request, Player.class);
        player.setWins(0);
        player.setLosses(0);
        player.setElo(0);
        player.setHoursPlayed(0);
        player.setRatingAdjustment(50);

        Player savedPlayer = playerRepository.save(player);

        PlayerResponseDTO responseDTO = modelMapper.map(savedPlayer, PlayerResponseDTO.class);
        responseDTO.setTeamId(null); // Since team is null at creation

        return responseDTO;
    }

    @Override
    public PlayerResponseDTO getPlayerById(UUID playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found"));

        PlayerResponseDTO responseDTO = modelMapper.map(player, PlayerResponseDTO.class);
        responseDTO.setTeamId(player.getTeam() != null ? player.getTeam().getId() : null);

        return responseDTO;
    }

    @Override
    public List<PlayerResponseDTO> getAllPlayers() {
        List<Player> players = playerRepository.findAll();

        return players.stream()
                .map(player -> {
                    PlayerResponseDTO dto = modelMapper.map(player, PlayerResponseDTO.class);
                    dto.setTeamId(player.getTeam() != null ? player.getTeam().getId() : null);
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
