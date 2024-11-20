package api.services;

import api.exceptions.DuplicateResourceException;
import api.exceptions.InvalidInputException;
import api.exceptions.ResourceNotFoundException;
import api.models.Player;
import api.models.Team;
import api.modelsDTO.CreateTeamRequestDTO;
import api.modelsDTO.PlayerResponseDTO;
import api.modelsDTO.TeamResponseDTO;
import api.repositories.PlayerRepositoryI;
import api.repositories.TeamRepositoryI;
import api.servicesInterface.TeamServiceI;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TeamService implements TeamServiceI {


    private final TeamRepositoryI teamRepository;
    private final PlayerRepositoryI playerRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public TeamService(TeamRepositoryI teamRepository, PlayerRepositoryI playerRepository, ModelMapper modelMapper) {
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    public TeamResponseDTO createTeam(CreateTeamRequestDTO request) {
        if (teamRepository.existsByTeamName(request.getTeamName())) {
            throw new DuplicateResourceException("Team name '" + request.getTeamName() + "' already exists.");
        }

        if (request.getPlayers().size() != 5) {
            throw new InvalidInputException("Team must have exactly 5 players.");
        }

        List<Player> players = playerRepository.findAllById(request.getPlayers());

        if (players.size() != 5) {
            throw new ResourceNotFoundException("One or more players not found");
        }

        for (Player player : players) {
            if (player.getTeam() != null) {
                throw new InvalidInputException("Player '" + player.getNickname() + "' is already in a team.");
            }
        }

        Team team = new Team();
        team.setTeamName(request.getTeamName());
        team.setPlayers(players);

        for (Player player : players) {
            player.setTeam(team);
        }

        Team savedTeam = teamRepository.save(team);

        TeamResponseDTO responseDTO = modelMapper.map(savedTeam, TeamResponseDTO.class);
        List<PlayerResponseDTO> playerResponseDTOs = players.stream()
                .map(player -> {
                    PlayerResponseDTO dto = modelMapper.map(player, PlayerResponseDTO.class);
                    dto.setTeamId(savedTeam.getId());
                    return dto;
                })
                .collect(Collectors.toList());
        responseDTO.setPlayers(playerResponseDTOs);

        return responseDTO;
    }

    @Override
    public TeamResponseDTO getTeamById(UUID teamId) {
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new ResourceNotFoundException("Team not found"));

        TeamResponseDTO responseDTO = modelMapper.map(team, TeamResponseDTO.class);
        List<PlayerResponseDTO> playerResponseDTOs = team.getPlayers().stream()
                .map(player -> {
                    PlayerResponseDTO dto = modelMapper.map(player, PlayerResponseDTO.class);
                    dto.setTeamId(team.getId());
                    return dto;
                })
                .collect(Collectors.toList());
        responseDTO.setPlayers(playerResponseDTOs);

        return responseDTO;
    }
}
