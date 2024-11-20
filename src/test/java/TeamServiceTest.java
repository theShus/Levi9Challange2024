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
import api.services.TeamService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
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

        // Mock the model mapper
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
        players.get(0).setTeam(new Team()); // First player is already in a team
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

}
