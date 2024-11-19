package api.businessLogic;

import api.businessLogicInterface.PlayerBLI;
import api.businessLogicInterface.TeamBLI;
import api.models.CommandResponse;
import api.servicesInterface.PlayerServiceI;
import api.servicesInterface.TeamServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TeamBL implements TeamBLI {

    private final TeamServiceI _service;

    @Autowired
    public TeamBL(TeamServiceI service) {
        this._service = service;
    }

    @Override
    public CommandResponse<Object> getTeams() {

        return null;
    }

    @Override
    public CommandResponse<Object> getTeamById() {
        return null;
    }
}
