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
import api.services.TeamService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class TeamServiceTest {
    @Mock
    private TeamRepositoryI teamRepository;

    @Mock
    private PlayerRepositoryI playerRepository;

    @Mock
    private MatchRepositoryI matchRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private TeamService teamService;


    @Test
    public void testCreateTeam_Success() {
        CreateTeamRequestDTO request = new CreateTeamRequestDTO();
        request.setTeamName("NewTeam");
        List<UUID> playerIds = Arrays.asList(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()
        );
        request.setPlayers(playerIds);

        when(teamRepository.existsByTeamName(request.getTeamName())).thenReturn(false);

        List<Player> players = createPlayers(playerIds, false);
        when(playerRepository.findAllById(playerIds)).thenReturn(players);

        Team team = new Team();
        team.setTeamName(request.getTeamName());
        team.setPlayers(players);

        Team savedTeam = new Team();
        savedTeam.setId(UUID.randomUUID());
        savedTeam.setTeamName(request.getTeamName());
        savedTeam.setPlayers(players);

        when(teamRepository.save(any(Team.class))).thenReturn(savedTeam);

        TeamResponseDTO responseDTO = new TeamResponseDTO();
        responseDTO.setId(savedTeam.getId());
        responseDTO.setTeamName(savedTeam.getTeamName());

        List<PlayerResponseDTO> playerResponseDTOs = createPlayerResponseDTOs(players, savedTeam.getId());
        responseDTO.setPlayers(playerResponseDTOs);

        when(modelMapper.map(savedTeam, TeamResponseDTO.class)).thenReturn(responseDTO);
        for (Player player : players) {
            PlayerResponseDTO playerDTO = playerResponseDTOs.stream()
                    .filter(dto -> dto.getId().equals(player.getId()))
                    .findFirst()
                    .orElse(null);
            when(modelMapper.map(player, PlayerResponseDTO.class)).thenReturn(playerDTO);
        }

        TeamResponseDTO result = teamService.createTeam(request);

        assertNotNull(result);
        assertEquals(responseDTO.getId(), result.getId());
        assertEquals(responseDTO.getTeamName(), result.getTeamName());
        assertEquals(responseDTO.getPlayers(), result.getPlayers());

        verify(teamRepository).existsByTeamName(request.getTeamName());
        verify(playerRepository).findAllById(playerIds);
        verify(teamRepository).save(any(Team.class));
    }

    @Test
    public void testCreateTeam_DuplicateTeamName() {
        CreateTeamRequestDTO request = new CreateTeamRequestDTO();
        request.setTeamName("ExistingTeam");
        request.setPlayers(Collections.nCopies(5, UUID.randomUUID()));

        when(teamRepository.existsByTeamName(request.getTeamName())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> {
            teamService.createTeam(request);
        });

        verify(playerRepository, never()).findAllById(anyList());
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    public void testCreateTeam_InvalidNumberOfPlayers() {
        CreateTeamRequestDTO request = new CreateTeamRequestDTO();
        request.setTeamName("NewTeam");
        request.setPlayers(Arrays.asList(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));

        when(teamRepository.existsByTeamName(request.getTeamName())).thenReturn(false);

        assertThrows(InvalidInputException.class, () -> {
            teamService.createTeam(request);
        });

        verify(playerRepository, never()).findAllById(anyList());
    }

    @Test
    public void testCreateTeam_PlayersNotFound() {
        CreateTeamRequestDTO request = new CreateTeamRequestDTO();
        request.setTeamName("NewTeam");
        List<UUID> playerIds = Arrays.asList(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()
        );
        request.setPlayers(playerIds);

        when(teamRepository.existsByTeamName(request.getTeamName())).thenReturn(false);

        List<Player> players = createPlayers(playerIds.subList(0, 3), false);
        when(playerRepository.findAllById(playerIds)).thenReturn(players);

        assertThrows(ResourceNotFoundException.class, () -> {
            teamService.createTeam(request);
        });
    }

    @Test
    public void testCreateTeam_PlayerAlreadyInTeam() {
        CreateTeamRequestDTO request = new CreateTeamRequestDTO();
        request.setTeamName("NewTeam");
        List<UUID> playerIds = Arrays.asList(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()
        );
        request.setPlayers(playerIds);

        when(teamRepository.existsByTeamName(request.getTeamName())).thenReturn(false);

        List<Player> players = createPlayers(playerIds, false);
        players.get(0).setTeam(new Team());
        when(playerRepository.findAllById(playerIds)).thenReturn(players);

        assertThrows(InvalidInputException.class, () -> {
            teamService.createTeam(request);
        });
    }

    // Helper methods
    private List<Player> createPlayers(List<UUID> playerIds, boolean assignTeams) {
        return playerIds.stream().map(id -> {
            Player player = new Player();
            player.setId(id);
            player.setNickname("Player_" + id.toString().substring(0, 5));
            if (assignTeams) {
                player.setTeam(new Team());
            } else {
                player.setTeam(null);
            }
            return player;
        }).collect(Collectors.toList());
    }

    private List<PlayerResponseDTO> createPlayerResponseDTOs(List<Player> players, UUID teamId) {
        return players.stream().map(player -> {
            PlayerResponseDTO dto = new PlayerResponseDTO();
            dto.setId(player.getId());
            dto.setNickname(player.getNickname());
            dto.setTeamId(teamId);
            return dto;
        }).collect(Collectors.toList());
    }


    @Test
    public void testUpdateTeam_Success() {
        UUID teamId = UUID.randomUUID();
        UpdateTeamRequestDTO request = new UpdateTeamRequestDTO();
        request.setTeamName("UpdatedTeamName");

        Team existingTeam = createTeam(teamId, "OldTeamName");
        Team updatedTeam = createTeam(teamId, "UpdatedTeamName");

        TeamResponseDTO responseDTO = new TeamResponseDTO();
        responseDTO.setId(teamId);
        responseDTO.setTeamName("UpdatedTeamName");
        responseDTO.setPlayers(new ArrayList<>());

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(existingTeam));
        when(teamRepository.existsByTeamName("UpdatedTeamName")).thenReturn(false);
        when(teamRepository.save(existingTeam)).thenReturn(updatedTeam);
        when(modelMapper.map(updatedTeam, TeamResponseDTO.class)).thenReturn(responseDTO);

        TeamResponseDTO result = teamService.updateTeam(teamId, request);

        assertNotNull(result);
        assertEquals("UpdatedTeamName", result.getTeamName());

        verify(teamRepository).findById(teamId);
        verify(teamRepository).existsByTeamName("UpdatedTeamName");
        verify(teamRepository).save(existingTeam);
        verify(modelMapper).map(updatedTeam, TeamResponseDTO.class);
    }

    @Test
    public void testUpdateTeam_TeamNotFound() {
        UUID teamId = UUID.randomUUID();
        UpdateTeamRequestDTO request = new UpdateTeamRequestDTO();
        request.setTeamName("UpdatedTeamName");

        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            teamService.updateTeam(teamId, request);
        });

        assertEquals("Team not found", exception.getMessage());

        verify(teamRepository).findById(teamId);
        verify(teamRepository, never()).existsByTeamName(anyString());
        verify(teamRepository, never()).save(any(Team.class));
        verifyNoInteractions(modelMapper);
    }

    @Test
    public void testUpdateTeam_DuplicateTeamName() {
        UUID teamId = UUID.randomUUID();
        UpdateTeamRequestDTO request = new UpdateTeamRequestDTO();
        request.setTeamName("ExistingTeamName");

        Team existingTeam = createTeam(teamId, "OldTeamName");

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(existingTeam));
        when(teamRepository.existsByTeamName("ExistingTeamName")).thenReturn(true);

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () -> {
            teamService.updateTeam(teamId, request);
        });

        assertEquals("Team name 'ExistingTeamName' already exists.", exception.getMessage());

        verify(teamRepository).findById(teamId);
        verify(teamRepository).existsByTeamName("ExistingTeamName");
        verify(teamRepository, never()).save(any(Team.class));
        verifyNoInteractions(modelMapper);
    }

    @Test
    public void testDeleteTeam_Success() {
        UUID teamId = UUID.randomUUID();
        Team existingTeam = createTeam(teamId, "TeamToDelete");
        existingTeam.setPlayers(new ArrayList<>());

        List<Match> matches = new ArrayList<>();

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(existingTeam));
        when(matchRepository.findByTeam1OrTeam2(existingTeam, existingTeam)).thenReturn(matches);

        assertDoesNotThrow(() -> teamService.deleteTeam(teamId));

        verify(teamRepository).findById(teamId);
        verify(matchRepository).findByTeam1OrTeam2(existingTeam, existingTeam);
        verify(teamRepository).delete(existingTeam);
    }

    @Test
    public void testDeleteTeam_TeamNotFound() {
        UUID teamId = UUID.randomUUID();

        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            teamService.deleteTeam(teamId);
        });

        assertEquals("Team not found", exception.getMessage());

        verify(teamRepository).findById(teamId);
        verify(teamRepository, never()).delete(any(Team.class));
        verify(matchRepository, never()).findByTeam1OrTeam2(any(Team.class), any(Team.class));
    }

    @Test
    public void testDeleteTeam_TeamHasPlayers() {
        UUID teamId = UUID.randomUUID();
        Team existingTeam = createTeam(teamId, "TeamWithPlayers");
        Player player = new Player();
        player.setId(UUID.randomUUID());
        player.setNickname("Player1");
        player.setTeam(existingTeam);
        existingTeam.setPlayers(Arrays.asList(player));

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(existingTeam));

        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
            teamService.deleteTeam(teamId);
        });

        assertEquals("Cannot delete team with assigned players.", exception.getMessage());

        verify(teamRepository).findById(teamId);
        verify(teamRepository, never()).delete(any(Team.class));
        verify(matchRepository, never()).findByTeam1OrTeam2(any(Team.class), any(Team.class));
    }

    @Test
    public void testDeleteTeam_TeamHasMatches() {
        UUID teamId = UUID.randomUUID();
        Team existingTeam = createTeam(teamId, "TeamWithMatches");
        existingTeam.setPlayers(new ArrayList<>());

        Match match = new Match();
        match.setId(UUID.randomUUID());
        match.setTeam1(existingTeam);
        match.setTeam2(new Team());

        List<Match> matches = Arrays.asList(match);

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(existingTeam));
        when(matchRepository.findByTeam1OrTeam2(existingTeam, existingTeam)).thenReturn(matches);

        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
            teamService.deleteTeam(teamId);
        });

        assertEquals("Cannot delete team with associated matches.", exception.getMessage());

        verify(teamRepository).findById(teamId);
        verify(matchRepository).findByTeam1OrTeam2(existingTeam, existingTeam);
        verify(teamRepository, never()).delete(any(Team.class));
    }

    @Test
    public void testSwapPlayers_Success() {
        UUID team1Id = UUID.randomUUID();
        UUID team2Id = UUID.randomUUID();

        UUID team1Player1Id = UUID.randomUUID();
        UUID team1Player2Id = UUID.randomUUID();
        UUID team1Player3Id = UUID.randomUUID();
        UUID team1Player4Id = UUID.randomUUID();
        UUID team1Player5Id = UUID.randomUUID();

        UUID team2Player1Id = UUID.randomUUID();
        UUID team2Player2Id = UUID.randomUUID();
        UUID team2Player3Id = UUID.randomUUID();
        UUID team2Player4Id = UUID.randomUUID();
        UUID team2Player5Id = UUID.randomUUID();

        SwapPlayersRequestDTO request = new SwapPlayersRequestDTO();
        request.setTeam1Id(team1Id);
        request.setTeam2Id(team2Id);
        request.setTeam1PlayerIds(Arrays.asList(team1Player1Id, team1Player2Id, team1Player3Id, team1Player4Id, team1Player5Id));
        request.setTeam2PlayerIds(Arrays.asList(team2Player1Id, team2Player2Id, team2Player3Id, team2Player4Id, team2Player5Id));

        Team team1 = createTeam(team1Id, "Team1");
        Team team2 = createTeam(team2Id, "Team2");

        Player team1Player1 = createPlayer(team1Player1Id, team1);
        Player team1Player2 = createPlayer(team1Player2Id, team1);
        Player team1Player3 = createPlayer(team1Player3Id, team1);
        Player team1Player4 = createPlayer(team1Player4Id, team1);
        Player team1Player5 = createPlayer(team1Player5Id, team1);

        Player team2Player1 = createPlayer(team2Player1Id, team2);
        Player team2Player2 = createPlayer(team2Player2Id, team2);
        Player team2Player3 = createPlayer(team2Player3Id, team2);
        Player team2Player4 = createPlayer(team2Player4Id, team2);
        Player team2Player5 = createPlayer(team2Player5Id, team2);

        when(teamRepository.findById(team1Id)).thenReturn(Optional.of(team1));
        when(teamRepository.findById(team2Id)).thenReturn(Optional.of(team2));

        when(playerRepository.findAllById(Arrays.asList(team1Player1Id, team1Player2Id, team1Player3Id, team1Player4Id, team1Player5Id)))
                .thenReturn(Arrays.asList(team1Player1, team1Player2, team1Player3, team1Player4, team1Player5));
        when(playerRepository.findAllById(Arrays.asList(team2Player1Id, team2Player2Id, team2Player3Id, team2Player4Id, team2Player5Id)))
                .thenReturn(Arrays.asList(team2Player1, team2Player2, team2Player3, team2Player4, team2Player5));

        assertDoesNotThrow(() -> teamService.swapPlayers(request));

        assertEquals(team2, team1Player1.getTeam());
        assertEquals(team2, team1Player2.getTeam());
        assertEquals(team1, team2Player1.getTeam());
        assertEquals(team1, team2Player2.getTeam());

        verify(playerRepository).saveAll(Arrays.asList(team1Player1, team1Player2, team1Player3, team1Player4, team1Player5));
        verify(playerRepository).saveAll(Arrays.asList(team2Player1, team2Player2, team2Player3, team2Player4, team2Player5));
    }

    @Test
    public void testSwapPlayers_Team1NotFound() {
        UUID team1Id = UUID.randomUUID();
        UUID team2Id = UUID.randomUUID();

        SwapPlayersRequestDTO request = new SwapPlayersRequestDTO();
        request.setTeam1Id(team1Id);
        request.setTeam2Id(team2Id);
        request.setTeam1PlayerIds(Arrays.asList(UUID.randomUUID()));
        request.setTeam2PlayerIds(Arrays.asList(UUID.randomUUID()));

        when(teamRepository.findById(team1Id)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            teamService.swapPlayers(request);
        });

        assertEquals("Team1 not found", exception.getMessage());

        verify(teamRepository).findById(team1Id);
        verify(teamRepository, never()).findById(team2Id);
        verify(playerRepository, never()).findAllById(anyList());
        verify(playerRepository, never()).saveAll(anyList());
    }

    @Test
    public void testSwapPlayers_Team2NotFound() {
        UUID team1Id = UUID.randomUUID();
        UUID team2Id = UUID.randomUUID();

        SwapPlayersRequestDTO request = new SwapPlayersRequestDTO();
        request.setTeam1Id(team1Id);
        request.setTeam2Id(team2Id);
        request.setTeam1PlayerIds(Arrays.asList(UUID.randomUUID()));
        request.setTeam2PlayerIds(Arrays.asList(UUID.randomUUID()));

        Team team1 = createTeam(team1Id, "Team1");
        when(teamRepository.findById(team1Id)).thenReturn(Optional.of(team1));
        when(teamRepository.findById(team2Id)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            teamService.swapPlayers(request);
        });

        assertEquals("Team2 not found", exception.getMessage());

        verify(teamRepository).findById(team1Id);
        verify(teamRepository).findById(team2Id);
        verify(playerRepository, never()).findAllById(anyList());
        verify(playerRepository, never()).saveAll(anyList());
    }

    @Test
    public void testSwapPlayers_SameTeamId() {
        UUID teamId = UUID.randomUUID();

        SwapPlayersRequestDTO request = new SwapPlayersRequestDTO();
        request.setTeam1Id(teamId);
        request.setTeam2Id(teamId);
        request.setTeam1PlayerIds(Arrays.asList(UUID.randomUUID()));
        request.setTeam2PlayerIds(Arrays.asList(UUID.randomUUID()));

        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
            teamService.swapPlayers(request);
        });

        assertEquals("Cannot swap players within the same team.", exception.getMessage());

        verify(teamRepository, never()).findById(any(UUID.class));
        verify(playerRepository, never()).findAllById(anyList());
        verify(playerRepository, never()).saveAll(anyList());
    }

    // Helper methods
    private Team createTeam(UUID teamId, String teamName) {
        Team team = new Team();
        team.setId(teamId);
        team.setTeamName(teamName);
        team.setPlayers(new ArrayList<>());
        return team;
    }

    private Player createPlayer(UUID playerId, Team team) {
        Player player = new Player();
        player.setId(playerId);
        player.setNickname("Player_" + playerId.toString().substring(0, 5));
        player.setTeam(team);
        player.setWins(0);
        player.setLosses(0);
        player.setElo(1500);
        player.setHoursPlayed(100);
        player.setRatingAdjustment(50);
        return player;
    }
}
