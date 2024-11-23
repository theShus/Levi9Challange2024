package api.services;

import api.config.LambdaInvoker;
import api.exceptions.InvalidInputException;
import api.exceptions.ResourceNotFoundException;
import api.models.Match;
import api.models.Player;
import api.models.Team;
import api.modelsDTO.CreateMatchRequestDTO;
import api.modelsDTO.MatchResponseDTO;
import api.repositories.MatchRepositoryI;
import api.repositories.PlayerRepositoryI;
import api.repositories.TeamRepositoryI;
import api.servicesInterface.MatchServiceI;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MatchService implements MatchServiceI {

    private final TeamRepositoryI teamRepository;
    private final MatchRepositoryI matchRepository;
    private final PlayerRepositoryI playerRepository;
    private final ModelMapper modelMapper;
    private final LambdaInvoker lambdaInvoker;
    private final ObjectMapper objectMapper;

    @Autowired
    public MatchService(TeamRepositoryI teamRepository, MatchRepositoryI matchRepository, PlayerRepositoryI playerRepository, ModelMapper modelMapper) {
        this.teamRepository = teamRepository;
        this.matchRepository = matchRepository;
        this.playerRepository = playerRepository;
        this.modelMapper = modelMapper;
        this.lambdaInvoker = new LambdaInvoker();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void createMatch(CreateMatchRequestDTO request) {
        if (request.getDuration() < 1) {
            throw new InvalidInputException("Duration must be at least 1");
        }

        if (request.getTeam1Id().equals(request.getTeam2Id())) {
            throw new InvalidInputException("Team1 and Team2 must be different teams");
        }

        Team team1 = teamRepository.findById(request.getTeam1Id())
                .orElseThrow(() -> new ResourceNotFoundException("Team 1 not found"));

        Team team2 = teamRepository.findById(request.getTeam2Id())
                .orElseThrow(() -> new ResourceNotFoundException("Team 2 not found"));

        Team winningTeam = null;
        if (request.getWinningTeamId() != null) {
            if (!request.getWinningTeamId().equals(team1.getId()) && !request.getWinningTeamId().equals(team2.getId())) {
                throw new InvalidInputException("Winning team must be either team1 or team2");
            }
            winningTeam = teamRepository.findById(request.getWinningTeamId())
                    .orElseThrow(() -> new ResourceNotFoundException("Winning team not found"));
        }

        Match match = new Match();
        match.setTeam1(team1);
        match.setTeam2(team2);
        match.setWinningTeam(winningTeam);
        match.setDuration(request.getDuration());

        if (!team1.isRandom() && !team2.isRandom()) {
            matchRepository.save(match);
        }

        try {
            String jsonString = objectMapper.writeValueAsString(match);
            lambdaInvoker.invokeSendEmailFunction(jsonString);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


        // Update player stats
        updatePlayerStats(team1.getPlayers(), team2.getPlayers(), winningTeam, request.getDuration());

        deleteRandomTeam(team1);
        deleteRandomTeam(team2);

    }

    // Metoda za dobijanje svih mečeva
    public List<MatchResponseDTO> getAllMatches() {
        List<Match> matches = matchRepository.findAll();
        return matches.stream()
                .map(match -> {
                    MatchResponseDTO dto = modelMapper.map(match, MatchResponseDTO.class);
                    dto.setTeam1Id(match.getTeam1().getId());
                    dto.setTeam2Id(match.getTeam2().getId());
                    dto.setWinningTeamId(match.getWinningTeam() != null ? match.getWinningTeam().getId() : null);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private void updatePlayerStats(List<Player> team1Players, List<Player> team2Players, Team winningTeam, int duration) {
        double S_team1, S_team2;

        if (winningTeam == null) {// Draw
            S_team1 = S_team2 = 0.5;
        } else if (winningTeam.getId().equals(team1Players.get(0).getTeam().getId())) {// Team1 won
            S_team1 = 1.0;
            S_team2 = 0.0;
            team1Players.forEach(player -> player.setWins(player.getWins() + 1));
            team2Players.forEach(player -> player.setLosses(player.getLosses() + 1));
        } else {// Team2 won
            S_team1 = 0.0;
            S_team2 = 1.0;
            team2Players.forEach(player -> player.setWins(player.getWins() + 1));
            team1Players.forEach(player -> player.setLosses(player.getLosses() + 1));
        }

        // Calculate avg elo
        int avgEloTeam1 = team1Players.stream().mapToInt(Player::getElo).sum() / team1Players.size();
        int avgEloTeam2 = team2Players.stream().mapToInt(Player::getElo).sum() / team2Players.size();

        // Update player elo
        for (Player player : team1Players)
            updatePlayerElo(player, avgEloTeam2, S_team1, duration);
        for (Player player : team2Players)
            updatePlayerElo(player, avgEloTeam1, S_team2, duration);
    }

    //https://calculator.academy/elo-rating-calculator/
    private void updatePlayerElo(Player player, int opponentAvgElo, double S, int duration) {
        double E = 1 / (1 + Math.pow(10, (opponentAvgElo - player.getElo()) / 400.0));

        // Determine K factor
        int hoursPlayed = player.getHoursPlayed() + duration;
        int K = getKFactor(hoursPlayed);

        // Update ELO
        int newElo = (int) Math.round(player.getElo() + K * (S - E));
        player.setElo(newElo);

        // Update hours played and rating adjustment
        player.setHoursPlayed(hoursPlayed);
        player.setRatingAdjustment(K);

        // Save player
        playerRepository.save(player);
    }

    private int getKFactor(int hoursPlayed) {
        if (hoursPlayed < 500) {
            return 50;
        } else if (hoursPlayed < 1000) {
            return 40;
        } else if (hoursPlayed < 3000) {
            return 30;
        } else if (hoursPlayed < 5000) {
            return 20;
        } else {
            return 10;
        }
    }

    public void deleteData() {
        List<Match> matches = matchRepository.findAll();
        matchRepository.deleteAll(matches);
    }

    private void deleteRandomTeam(Team team) {
        if (team.isRandom()) {
            teamRepository.delete(team);
        }
    }

}
