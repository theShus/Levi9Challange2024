import api.exceptions.InvalidInputException;
import api.exceptions.ResourceNotFoundException;
import api.models.Match;
import api.models.Player;
import api.models.Team;
import api.modelsDTO.CreateMatchRequestDTO;
import api.repositories.MatchRepositoryI;
import api.repositories.PlayerRepositoryI;
import api.repositories.TeamRepositoryI;
import api.services.MatchService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class MatchServiceTest {
    @Mock
    private TeamRepositoryI teamRepository;

    @Mock
    private MatchRepositoryI matchRepository;

    @Mock
    private PlayerRepositoryI playerRepository;

    @InjectMocks
    private MatchService matchService;

    @Test
    public void testCreateMatch_Success() {
        UUID team1Id = UUID.randomUUID();
        UUID team2Id = UUID.randomUUID();
        UUID winningTeamId = team1Id;
        int duration = 2;

        CreateMatchRequestDTO request = new CreateMatchRequestDTO();
        request.setTeam1Id(team1Id);
        request.setTeam2Id(team2Id);
        request.setWinningTeamId(winningTeamId);
        request.setDuration(duration);

        Team team1 = createTeam(team1Id);
        Team team2 = createTeam(team2Id);

        List<Player> team1Players = createPlayersForTeam(team1);
        List<Player> team2Players = createPlayersForTeam(team2);

        team1.setPlayers(team1Players);
        team2.setPlayers(team2Players);

        when(teamRepository.findById(team1Id)).thenReturn(Optional.of(team1));
        when(teamRepository.findById(team2Id)).thenReturn(Optional.of(team2));
        when(teamRepository.findById(winningTeamId)).thenReturn(Optional.of(team1));

        matchService.createMatch(request);

        verify(matchRepository).save(any(Match.class));

        verify(playerRepository, times(team1Players.size() + team2Players.size())).save(any(Player.class));

        for (Player player : team1Players) {
            assertEquals(1, player.getWins());
            assertEquals(0, player.getLosses());
            assertEquals(duration, player.getHoursPlayed());
            assertTrue(player.getElo() > 0);
        }

        for (Player player : team2Players) {
            assertEquals(0, player.getWins());
            assertEquals(1, player.getLosses());
            assertEquals(duration, player.getHoursPlayed());
            assertTrue(player.getElo() < 0);
        }
    }

    @Test
    public void testCreateMatch_InvalidDuration() {
        CreateMatchRequestDTO request = new CreateMatchRequestDTO();
        request.setDuration(0);

        assertThrows(InvalidInputException.class, () -> matchService.createMatch(request));

        verifyNoInteractions(teamRepository);
        verifyNoInteractions(matchRepository);
        verifyNoInteractions(playerRepository);
    }

    @Test
    public void testCreateMatch_TeamNotFound() {
        UUID team1Id = UUID.randomUUID();
        UUID team2Id = UUID.randomUUID();

        CreateMatchRequestDTO request = new CreateMatchRequestDTO();
        request.setTeam1Id(team1Id);
        request.setTeam2Id(team2Id);
        request.setDuration(2);

        when(teamRepository.findById(team1Id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> matchService.createMatch(request));

        verify(teamRepository).findById(team1Id);

        verify(teamRepository, never()).findById(team2Id);
        verifyNoInteractions(matchRepository);
        verifyNoInteractions(playerRepository);
    }

    @Test
    public void testCreateMatch_InvalidWinningTeam() {
        UUID team1Id = UUID.randomUUID();
        UUID team2Id = UUID.randomUUID();
        UUID invalidWinningTeamId = UUID.randomUUID();

        CreateMatchRequestDTO request = new CreateMatchRequestDTO();
        request.setTeam1Id(team1Id);
        request.setTeam2Id(team2Id);
        request.setWinningTeamId(invalidWinningTeamId);
        request.setDuration(2);

        Team team1 = createTeam(team1Id);
        Team team2 = createTeam(team2Id);

        when(teamRepository.findById(team1Id)).thenReturn(Optional.of(team1));
        when(teamRepository.findById(team2Id)).thenReturn(Optional.of(team2));

        assertThrows(InvalidInputException.class, () -> matchService.createMatch(request));

        verify(teamRepository).findById(team1Id);
        verify(teamRepository).findById(team2Id);
        verify(teamRepository, never()).findById(invalidWinningTeamId);
        verifyNoInteractions(matchRepository);
        verifyNoInteractions(playerRepository);
    }


    // Helper methods
    private Team createTeam(UUID teamId) {
        Team team = new Team();
        team.setId(teamId);
        team.setTeamName("Team_" + teamId.toString().substring(0, 5));
        return team;
    }

    private List<Player> createPlayersForTeam(Team team) {
        return IntStream.range(0, 5).mapToObj(i -> {
            Player player = new Player();
            player.setId(UUID.randomUUID());
            player.setNickname("Player_" + i);
            player.setTeam(team);
            player.setElo(0);
            player.setWins(0);
            player.setLosses(0);
            player.setHoursPlayed(0);
            return player;
        }).collect(Collectors.toList());
    }


}