package api.controllers;

import api.businessLogic.PlayerBL;
import api.businessLogicInterface.PlayerBLI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/matches")
@CrossOrigin()
public class MatchesController {

    private final PlayerBLI _businessLogic;

    @Autowired
    public MatchesController(PlayerBL businessLogic) {
        this._businessLogic = businessLogic;
    }

    @GetMapping()
    public ResponseEntity<Void> getMatches() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check")
    public ResponseEntity<Void> serviceCheck() {
        return ResponseEntity.ok().build();
    }

}

