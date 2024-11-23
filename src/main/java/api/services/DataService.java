package api.services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class DataService {
    private final PlayerService playerService;
    private final TeamService teamService;
    private final MatchService matchService;


    public DataService(PlayerService playerService, TeamService teamService, MatchService matchService) {
        this.playerService = playerService;
        this.teamService = teamService;
        this.matchService = matchService;
    }

    @Transactional
    public void deleteAllData(){
        matchService.deleteData();
        teamService.deleteData();
        playerService.deleteData();
    }
}
