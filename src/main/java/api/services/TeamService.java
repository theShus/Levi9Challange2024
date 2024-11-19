package api.services;

import api.models.CommandResponse;
import api.servicesInterface.PlayerServiceI;
import api.servicesInterface.TeamServiceI;
import org.springframework.stereotype.Service;

import java.net.http.HttpClient;

@Service
public class TeamService implements TeamServiceI {


    public TeamService() {
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
