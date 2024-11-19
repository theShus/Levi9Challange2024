package api.controllers;

import api.businessLogic.PlayerBL;
import api.businessLogicInterface.PlayerBLI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teams")
@CrossOrigin()
public class TeamsController {

    private final PlayerBLI _businessLogic;

    @Autowired
    public TeamsController(PlayerBL businessLogic) {
        this._businessLogic = businessLogic;
    }

    @GetMapping()
    public ResponseEntity<Void> getTeams() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("{id}")
    public ResponseEntity<Void> getTeamById(@PathVariable String id) {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check")
    public ResponseEntity<Void> serviceCheck() {
        return ResponseEntity.ok().build();
    }

}

