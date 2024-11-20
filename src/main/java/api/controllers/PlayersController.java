package api.controllers;

import api.modelsDTO.CreatePlayerRequestDTO;
import api.modelsDTO.PlayerResponseDTO;
import api.servicesInterface.PlayerServiceI;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/players")
@CrossOrigin()
public class PlayersController {

    private final PlayerServiceI playerService;

    @Autowired
    public PlayersController(PlayerServiceI playerService) {
        this.playerService = playerService;
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
}

