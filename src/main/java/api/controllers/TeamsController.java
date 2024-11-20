package api.controllers;

import api.modelsDTO.CreateTeamRequestDTO;
import api.modelsDTO.SwapPlayersRequestDTO;
import api.modelsDTO.TeamResponseDTO;
import api.servicesInterface.TeamServiceI;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/teams")
@CrossOrigin()
public class TeamsController {

    private final TeamServiceI teamService;

    @Autowired
    public TeamsController(TeamServiceI teamService) {
        this.teamService = teamService;
    }


    @PostMapping
    public ResponseEntity<TeamResponseDTO> createTeam(@Valid @RequestBody CreateTeamRequestDTO request) {
        TeamResponseDTO response = teamService.createTeam(request);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamResponseDTO> getTeamById(@PathVariable UUID id) {
        TeamResponseDTO response = teamService.getTeamById(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable("id") UUID teamId) {
        teamService.deleteTeam(teamId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/swap-players")
    public ResponseEntity<Void> swapPlayers(@Valid @RequestBody SwapPlayersRequestDTO request) {
        teamService.swapPlayers(request);
        return ResponseEntity.ok().build();
    }
}

