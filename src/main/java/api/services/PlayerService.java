package api.services;

import api.exceptions.DuplicateResourceException;
import api.exceptions.InvalidInputException;
import api.exceptions.ResourceNotFoundException;
import api.models.Player;
import api.modelsDTO.CreatePlayerRequestDTO;
import api.modelsDTO.PlayerResponseDTO;
import api.modelsDTO.UpdatePlayerRequestDTO;
import api.repositories.PlayerRepositoryI;
import api.servicesInterface.PlayerServiceI;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
            throw new DuplicateResourceException("Nickname '" + request.getNickname() + "' already exists.");
        }

        Player player = modelMapper.map(request, Player.class);
        player.setWins(0);
        player.setLosses(0);
        player.setElo(0);
        player.setHoursPlayed(0);
        player.setRatingAdjustment(50);

        Player savedPlayer = playerRepository.save(player);

        PlayerResponseDTO responseDTO = modelMapper.map(savedPlayer, PlayerResponseDTO.class);
        responseDTO.setTeamId(null); // team is null at creation

        return responseDTO;
    }

    @Override
    public PlayerResponseDTO getPlayerById(UUID playerId) {
        Player player = playerRepository.findById(playerId).orElseThrow(() -> new ResourceNotFoundException("Player not found"));

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


    public PlayerResponseDTO updatePlayer(UUID playerId, UpdatePlayerRequestDTO request) {
        Player existingPlayer = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found"));

        if (!existingPlayer.getNickname().equals(request.getNickname()) &&
                playerRepository.existsByNickname(request.getNickname())) {
            throw new DuplicateResourceException("Nickname '" + request.getNickname() + "' already exists.");
        }

        existingPlayer.setNickname(request.getNickname());
        existingPlayer.setWins(request.getWins());
        existingPlayer.setLosses(request.getLosses());
        existingPlayer.setElo(request.getElo());
        existingPlayer.setHoursPlayed(request.getHoursPlayed());

        Player updatedPlayer = playerRepository.save(existingPlayer);

        PlayerResponseDTO responseDTO = modelMapper.map(updatedPlayer, PlayerResponseDTO.class);
        responseDTO.setTeamId(updatedPlayer.getTeam() != null ? updatedPlayer.getTeam().getId() : null);

        return responseDTO;
    }

    public void deletePlayer(UUID playerId) {
        Player existingPlayer = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found"));

        if (existingPlayer.getTeam() != null) {
            throw new InvalidInputException("Cannot delete player who is part of a team.");
        }

        playerRepository.delete(existingPlayer);
    }


    public void deleteData() {
        List<Player> players = playerRepository.findAll();
        playerRepository.deleteAll(players);
    }
}
