import api.exceptions.DuplicateResourceException;
import api.exceptions.InvalidInputException;
import api.exceptions.ResourceNotFoundException;
import api.models.Player;
import api.models.Team;
import api.modelsDTO.CreatePlayerRequestDTO;
import api.modelsDTO.PlayerResponseDTO;
import api.modelsDTO.UpdatePlayerRequestDTO;
import api.repositories.PlayerRepositoryI;
import api.services.PlayerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(SpringExtension.class)
public class PlayerServiceTest {

    @Mock
    private PlayerRepositoryI playerRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PlayerService playerService;

    @Test
    public void testCreatePlayer_Success() {
        CreatePlayerRequestDTO request = new CreatePlayerRequestDTO();
        request.setNickname("NewPlayer");

        when(playerRepository.existsByNickname(request.getNickname())).thenReturn(false);

        Player player = new Player();
        player.setNickname(request.getNickname());

        Player savedPlayer = new Player();
        savedPlayer.setId(UUID.randomUUID());
        savedPlayer.setNickname(request.getNickname());
        savedPlayer.setWins(0);
        savedPlayer.setLosses(0);
        savedPlayer.setElo(0);
        savedPlayer.setHoursPlayed(0);
        savedPlayer.setRatingAdjustment(50);

        when(modelMapper.map(request, Player.class)).thenReturn(player);
        when(playerRepository.save(player)).thenReturn(savedPlayer);

        PlayerResponseDTO responseDTO = new PlayerResponseDTO();
        responseDTO.setId(savedPlayer.getId());
        responseDTO.setNickname(savedPlayer.getNickname());
        responseDTO.setWins(savedPlayer.getWins());
        responseDTO.setLosses(savedPlayer.getLosses());
        responseDTO.setElo(savedPlayer.getElo());
        responseDTO.setHoursPlayed(savedPlayer.getHoursPlayed());
        responseDTO.setTeamId(null);

        when(modelMapper.map(savedPlayer, PlayerResponseDTO.class)).thenReturn(responseDTO);

        PlayerResponseDTO result = playerService.createPlayer(request);

        assertNotNull(result);
        assertEquals(responseDTO.getId(), result.getId());
        assertEquals(responseDTO.getNickname(), result.getNickname());
        assertEquals(responseDTO.getWins(), result.getWins());
        assertEquals(responseDTO.getLosses(), result.getLosses());
        assertEquals(responseDTO.getElo(), result.getElo());
        assertEquals(responseDTO.getHoursPlayed(), result.getHoursPlayed());
        assertEquals(responseDTO.getTeamId(), result.getTeamId());

        verify(playerRepository).existsByNickname(request.getNickname());
        verify(playerRepository).save(player);
    }

    @Test
    public void testCreatePlayer_DuplicateNickname() {
        CreatePlayerRequestDTO request = new CreatePlayerRequestDTO();
        request.setNickname("ExistingPlayer");

        when(playerRepository.existsByNickname(request.getNickname())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> {
            playerService.createPlayer(request);
        });

        verify(playerRepository, never()).save(any(Player.class));
    }

    @Test
    public void testGetPlayerById_PlayerExists() {
        UUID playerId = UUID.randomUUID();

        Player player = new Player();
        player.setId(playerId);
        player.setNickname("TestPlayer");
        player.setWins(5);
        player.setLosses(2);
        player.setElo(1500);
        player.setHoursPlayed(100);

        PlayerResponseDTO responseDTO = new PlayerResponseDTO();
        responseDTO.setId(playerId);
        responseDTO.setNickname("TestPlayer");
        responseDTO.setWins(5);
        responseDTO.setLosses(2);
        responseDTO.setElo(1500);
        responseDTO.setHoursPlayed(100);
        responseDTO.setTeamId(null);

        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));
        when(modelMapper.map(player, PlayerResponseDTO.class)).thenReturn(responseDTO);

        PlayerResponseDTO result = playerService.getPlayerById(playerId);

        assertNotNull(result);
        assertEquals(responseDTO.getId(), result.getId());
        assertEquals(responseDTO.getNickname(), result.getNickname());
        assertEquals(responseDTO.getWins(), result.getWins());
        assertEquals(responseDTO.getLosses(), result.getLosses());
        assertEquals(responseDTO.getElo(), result.getElo());
        assertEquals(responseDTO.getHoursPlayed(), result.getHoursPlayed());
        assertEquals(responseDTO.getTeamId(), result.getTeamId());
    }

    @Test
    public void testGetPlayerById_PlayerDoesNotExist() {
        UUID playerId = UUID.randomUUID();
        when(playerRepository.findById(playerId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            playerService.getPlayerById(playerId);
        });
    }

    @Test
    public void testGetAllPlayers_PlayersExist() {
        Player player1 = new Player();
        player1.setId(UUID.randomUUID());
        player1.setNickname("PlayerOne");

        Player player2 = new Player();
        player2.setId(UUID.randomUUID());
        player2.setNickname("PlayerTwo");

        List<Player> players = Arrays.asList(player1, player2);

        when(playerRepository.findAll()).thenReturn(players);

        PlayerResponseDTO dto1 = new PlayerResponseDTO();
        dto1.setId(player1.getId());
        dto1.setNickname(player1.getNickname());
        dto1.setTeamId(null);

        PlayerResponseDTO dto2 = new PlayerResponseDTO();
        dto2.setId(player2.getId());
        dto2.setNickname(player2.getNickname());
        dto2.setTeamId(null);

        when(modelMapper.map(player1, PlayerResponseDTO.class)).thenReturn(dto1);
        when(modelMapper.map(player2, PlayerResponseDTO.class)).thenReturn(dto2);

        List<PlayerResponseDTO> result = playerService.getAllPlayers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(dto1));
        assertTrue(result.contains(dto2));

        // Verify that the repository method was called
        verify(playerRepository).findAll();
    }

    @Test
    public void testGetAllPlayers_NoPlayersExist() {
        when(playerRepository.findAll()).thenReturn(Collections.emptyList());

        List<PlayerResponseDTO> result = playerService.getAllPlayers();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(playerRepository).findAll();

        verify(modelMapper, never()).map(any(Player.class), eq(PlayerResponseDTO.class));
    }


    @Test
    public void testUpdatePlayer_Success() {
        // Arrange
        UUID playerId = UUID.randomUUID();
        UpdatePlayerRequestDTO request = new UpdatePlayerRequestDTO();
        request.setNickname("UpdatedNickname");
        request.setWins(15);
        request.setLosses(7);
        request.setElo(1550);
        request.setHoursPlayed(150);

        Player existingPlayer = new Player();
        existingPlayer.setId(playerId);
        existingPlayer.setNickname("OldNickname");
        existingPlayer.setWins(10);
        existingPlayer.setLosses(5);
        existingPlayer.setElo(1500);
        existingPlayer.setHoursPlayed(100);
        existingPlayer.setTeam(null);

        Player updatedPlayer = new Player();
        updatedPlayer.setId(playerId);
        updatedPlayer.setNickname("UpdatedNickname");
        updatedPlayer.setWins(15);
        updatedPlayer.setLosses(7);
        updatedPlayer.setElo(1550);
        updatedPlayer.setHoursPlayed(150);
        updatedPlayer.setTeam(null);

        PlayerResponseDTO responseDTO = new PlayerResponseDTO();
        responseDTO.setId(updatedPlayer.getId());
        responseDTO.setNickname(updatedPlayer.getNickname());
        responseDTO.setWins(updatedPlayer.getWins());
        responseDTO.setLosses(updatedPlayer.getLosses());
        responseDTO.setElo(updatedPlayer.getElo());
        responseDTO.setHoursPlayed(updatedPlayer.getHoursPlayed());
        responseDTO.setTeamId(null);

        when(playerRepository.findById(playerId)).thenReturn(Optional.of(existingPlayer));
        when(playerRepository.existsByNickname("UpdatedNickname")).thenReturn(false);
        when(playerRepository.save(existingPlayer)).thenReturn(updatedPlayer);
        when(modelMapper.map(updatedPlayer, PlayerResponseDTO.class)).thenReturn(responseDTO);

        // Act
        PlayerResponseDTO result = playerService.updatePlayer(playerId, request);

        // Assert
        assertNotNull(result);
        assertEquals(updatedPlayer.getId(), result.getId());
        assertEquals("UpdatedNickname", result.getNickname());
        assertEquals(15, result.getWins());
        assertEquals(7, result.getLosses());
        assertEquals(1550, result.getElo());
        assertEquals(150, result.getHoursPlayed());
        assertNull(result.getTeamId());

        // Verify interactions
        verify(playerRepository).findById(playerId);
        verify(playerRepository).existsByNickname("UpdatedNickname");
        verify(playerRepository).save(existingPlayer);
        verify(modelMapper).map(updatedPlayer, PlayerResponseDTO.class);
    }

    @Test
    public void testUpdatePlayer_PlayerNotFound() {
        // Arrange
        UUID playerId = UUID.randomUUID();
        UpdatePlayerRequestDTO request = new UpdatePlayerRequestDTO();
        request.setNickname("NonExistentPlayer");
        request.setWins(15);
        request.setLosses(7);
        request.setElo(1550);
        request.setHoursPlayed(150);

        when(playerRepository.findById(playerId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            playerService.updatePlayer(playerId, request);
        });

        assertEquals("Player not found", exception.getMessage());

        // Verify interactions
        verify(playerRepository).findById(playerId);
        verify(playerRepository, never()).existsByNickname(anyString());
        verify(playerRepository, never()).save(any(Player.class));
        verifyNoInteractions(modelMapper);
    }

    @Test
    public void testUpdatePlayer_DuplicateNickname() {
        // Arrange
        UUID playerId = UUID.randomUUID();
        UpdatePlayerRequestDTO request = new UpdatePlayerRequestDTO();
        request.setNickname("ExistingNickname");
        request.setWins(15);
        request.setLosses(7);
        request.setElo(1550);
        request.setHoursPlayed(150);

        Player existingPlayer = new Player();
        existingPlayer.setId(playerId);
        existingPlayer.setNickname("OldNickname");
        existingPlayer.setWins(10);
        existingPlayer.setLosses(5);
        existingPlayer.setElo(1500);
        existingPlayer.setHoursPlayed(100);
        existingPlayer.setTeam(null);

        when(playerRepository.findById(playerId)).thenReturn(Optional.of(existingPlayer));
        when(playerRepository.existsByNickname("ExistingNickname")).thenReturn(true);

        // Act & Assert
        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () -> {
            playerService.updatePlayer(playerId, request);
        });

        assertEquals("Nickname 'ExistingNickname' already exists.", exception.getMessage());

        // Verify interactions
        verify(playerRepository).findById(playerId);
        verify(playerRepository).existsByNickname("ExistingNickname");
        verify(playerRepository, never()).save(any(Player.class));
        verifyNoInteractions(modelMapper);
    }

    @Test
    public void testDeletePlayer_Success() {
        // Arrange
        UUID playerId = UUID.randomUUID();
        Player existingPlayer = new Player();
        existingPlayer.setId(playerId);
        existingPlayer.setNickname("PlayerToDelete");
        existingPlayer.setTeam(null); // Player is not in a team

        when(playerRepository.findById(playerId)).thenReturn(Optional.of(existingPlayer));
        doNothing().when(playerRepository).delete(existingPlayer);

        // Act
        assertDoesNotThrow(() -> playerService.deletePlayer(playerId));

        // Assert
        verify(playerRepository).findById(playerId);
        verify(playerRepository).delete(existingPlayer);
    }

    @Test
    public void testDeletePlayer_PlayerNotFound() {
        // Arrange
        UUID playerId = UUID.randomUUID();

        when(playerRepository.findById(playerId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            playerService.deletePlayer(playerId);
        });

        assertEquals("Player not found", exception.getMessage());

        // Verify interactions
        verify(playerRepository).findById(playerId);
        verify(playerRepository, never()).delete(any(Player.class));
    }

    @Test
    public void testDeletePlayer_PlayerInTeam() {
        // Arrange
        UUID playerId = UUID.randomUUID();
        Team team = new Team();
        team.setId(UUID.randomUUID());
        team.setTeamName("TeamA");

        Player existingPlayer = new Player();
        existingPlayer.setId(playerId);
        existingPlayer.setNickname("PlayerInTeam");
        existingPlayer.setTeam(team); // Player is in a team

        when(playerRepository.findById(playerId)).thenReturn(Optional.of(existingPlayer));

        // Act & Assert
        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
            playerService.deletePlayer(playerId);
        });

        assertEquals("Cannot delete player who is part of a team.", exception.getMessage());

        // Verify interactions
        verify(playerRepository).findById(playerId);
        verify(playerRepository, never()).delete(any(Player.class));
    }
}
