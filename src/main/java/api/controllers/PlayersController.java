package api.controllers;

import api.modelsDTO.CreatePlayerRequestDTO;
import api.modelsDTO.PlayerResponseDTO;
import api.modelsDTO.UpdatePlayerRequestDTO;
import api.services.DataService;
import api.servicesInterface.PlayerServiceI;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/players")
@CrossOrigin()
public class PlayersController {

    private final PlayerServiceI playerService;
    private final DataService dataService;

    @Autowired
    public PlayersController(PlayerServiceI playerService, DataService dataService) {
        this.playerService = playerService;
        this.dataService = dataService;
    }


    @PostMapping("/create")
    public ResponseEntity<PlayerResponseDTO> createPlayer(@Valid @RequestBody CreatePlayerRequestDTO request) {
        PlayerResponseDTO response = playerService.createPlayer(request);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerResponseDTO> getPlayerById(@PathVariable UUID id) {
        PlayerResponseDTO response = playerService.getPlayerById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<PlayerResponseDTO>> getAllPlayers() {
        List<PlayerResponseDTO> response = playerService.getAllPlayers();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlayerResponseDTO> updatePlayer(
            @PathVariable("id") UUID playerId,
            @Valid @RequestBody UpdatePlayerRequestDTO request) {
        PlayerResponseDTO updatedPlayer = playerService.updatePlayer(playerId, request);
        return ResponseEntity.ok(updatedPlayer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable("id") UUID playerId) {
        playerService.deletePlayer(playerId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{player_id}/leave_team")
    public ResponseEntity<PlayerResponseDTO> leaveTeam(@PathVariable("player_id") UUID playerId) {
        PlayerResponseDTO playerDTO = playerService.leaveTeam(playerId);
        return ResponseEntity.ok(playerDTO);
    }

    @DeleteMapping()
    @ResponseStatus(HttpStatus.OK)
    public void deleteData() {
        dataService.deleteAllData();
    }
}

