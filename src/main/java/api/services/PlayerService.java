package api.services;

import api.models.CommandResponse;
import api.servicesInterface.PlayerServiceI;
import org.springframework.stereotype.Service;

import java.net.http.HttpClient;

@Service
public class PlayerService implements PlayerServiceI {

    public PlayerService() {
    }

    @Override
    public CommandResponse<Object> create() {
        return null;
    }

    @Override
    public CommandResponse<Object> getPlayerById() {
        return null;
    }


}
