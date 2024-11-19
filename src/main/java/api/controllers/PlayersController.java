package api.controllers;

import api.businessLogic.PlayerBL;
import api.businessLogicInterface.PlayerBLI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/players")
@CrossOrigin()
public class PlayersController {

    private final PlayerBLI _businessLogic;

    @Autowired
    public PlayersController(PlayerBL businessLogic) {
        this._businessLogic = businessLogic;
    }

    @PostMapping("create")
    public ResponseEntity<Void> createPlayer() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("{id}")
    public ResponseEntity<Void> getPlayerById(@PathVariable String id) {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check")
    public ResponseEntity<Void> serviceCheck() {
        return ResponseEntity.ok().build();
    }

}

