package api.controllers;

import api.modelsDTO.CreateMatchRequestDTO;
import api.servicesInterface.MatchServiceI;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/matches")
@CrossOrigin()
public class MatchesController {

    private final MatchServiceI matchService;

    @Autowired
    public MatchesController(MatchServiceI matchService) {
        this.matchService = matchService;
    }

    @PostMapping
    public ResponseEntity<String> createMatch(@Valid @RequestBody CreateMatchRequestDTO request) {
        matchService.createMatch(request);
        return ResponseEntity.ok("Match created successfully");
    }
}

