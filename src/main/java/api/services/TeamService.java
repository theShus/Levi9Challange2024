package api.services;

import api.exceptions.DuplicateResourceException;
import api.exceptions.InvalidInputException;
import api.exceptions.ResourceNotFoundException;
import api.models.Match;
import api.models.Player;
import api.models.Team;
import api.modelsDTO.*;
import api.repositories.MatchRepositoryI;
import api.repositories.PlayerRepositoryI;
import api.repositories.TeamRepositoryI;
import api.servicesInterface.TeamServiceI;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TeamService implements TeamServiceI {


    private final TeamRepositoryI teamRepository;
    private final PlayerRepositoryI playerRepository;
    private final MatchRepositoryI matchRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public TeamService(TeamRepositoryI teamRepository, PlayerRepositoryI playerRepository, ModelMapper modelMapper, MatchRepositoryI matchRepositoryI) {
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
        this.modelMapper = modelMapper;
        this.matchRepository = matchRepositoryI;
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
            player.setRatingAdjustment(50);
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


    public TeamResponseDTO updateTeam(UUID teamId, UpdateTeamRequestDTO request) {
        Team existingTeam = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));

        if (!existingTeam.getTeamName().equals(request.getTeamName()) &&
                teamRepository.existsByTeamName(request.getTeamName())) {
            throw new DuplicateResourceException("Team name '" + request.getTeamName() + "' already exists.");
        }

        existingTeam.setTeamName(request.getTeamName());

        Team updatedTeam = teamRepository.save(existingTeam);

        TeamResponseDTO responseDTO = modelMapper.map(updatedTeam, TeamResponseDTO.class);
        responseDTO.setPlayers(updatedTeam.getPlayers().stream()
                .map(player -> modelMapper.map(player, PlayerResponseDTO.class))
                .collect(Collectors.toList()));

        return responseDTO;
    }

    public void deleteTeam(UUID teamId) {
        Team existingTeam = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));

        if (!existingTeam.getPlayers().isEmpty()) {
            throw new InvalidInputException("Cannot delete team with assigned players.");
        }

        // Optionally, check for matches involving this team
        List<Match> matches = matchRepository.findByTeam1OrTeam2(existingTeam, existingTeam);
        if (!matches.isEmpty()) {
            throw new InvalidInputException("Cannot delete team with associated matches.");
        }

        teamRepository.delete(existingTeam);
    }

    public void swapPlayers(SwapPlayersRequestDTO request) {
        // Validacija da su timovi različiti
        if (request.getTeam1Id().equals(request.getTeam2Id())) {
            throw new InvalidInputException("Cannot swap players within the same team.");
        }

        // Pronalaženje timova
        Team team1 = teamRepository.findById(request.getTeam1Id())
                .orElseThrow(() -> new ResourceNotFoundException("Team1 not found"));

        Team team2 = teamRepository.findById(request.getTeam2Id())
                .orElseThrow(() -> new ResourceNotFoundException("Team2 not found"));

        // Provera da li igrači pripadaju odgovarajućim timovima
        List<Player> team1PlayersToSwap = playerRepository.findAllById(request.getTeam1PlayerIds());
        if (team1PlayersToSwap.size() != request.getTeam1PlayerIds().size()) {
            throw new ResourceNotFoundException("One or more players from Team1 not found.");
        }

        List<Player> team2PlayersToSwap = playerRepository.findAllById(request.getTeam2PlayerIds());
        if (team2PlayersToSwap.size() != request.getTeam2PlayerIds().size()) {
            throw new ResourceNotFoundException("One or more players from Team2 not found.");
        }

        // Provera da li igrači pripadaju timovima
        for (Player player : team1PlayersToSwap) {
            if (!player.getTeam().getId().equals(team1.getId())) {
                throw new InvalidInputException("Player " + player.getNickname() + " does not belong to Team1.");
            }
        }

        for (Player player : team2PlayersToSwap) {
            if (!player.getTeam().getId().equals(team2.getId())) {
                throw new InvalidInputException("Player " + player.getNickname() + " does not belong to Team2.");
            }
        }

        // Zamena igrača
        for (Player player : team1PlayersToSwap) {
            player.setTeam(team2);
        }

        for (Player player : team2PlayersToSwap) {
            player.setTeam(team1);
        }

        // Sačuvaj promene
        playerRepository.saveAll(team1PlayersToSwap);
        playerRepository.saveAll(team2PlayersToSwap);
    }

    @Override
    public TeamResponseDTO generateTeams(Integer teamSize) {
        int playersNumber = teamSize * 2;
        Set<Player> allPlayersForMatch = playerRepository.findAllForGeneratedTeam(playersNumber);

        if (allPlayersForMatch.size() < playersNumber)
            throw new RuntimeException("Not enough players for this match");




        return null;
    }

    public void deleteData() {
        List<Team> teams = teamRepository.findAll();
        teamRepository.deleteAll(teams);
    }

    private void generateTeams(Set<Player> players) {
        List<Player> listOfPlayer = new ArrayList<>(players);

        Set<Player> team1 = new HashSet<>();
        Set<Player> team2 = new HashSet<>();

        int totalPlayers = listOfPlayer.size();
        int N = totalPlayers / 2;

        int left = 0;
        int right = totalPlayers - 1;

        while (left <= right) {
            team1.add(listOfPlayer.get(left));
            if (left != right) {
                team1.add(listOfPlayer.get(right));
            }
            left++;
            right--;

            if (left <= right) {
                team2.add(listOfPlayer.get(left));
                if (left != right) {
                    team2.add(listOfPlayer.get(right));
                }
                left++;
                right--;
            }
        }
    }
}
