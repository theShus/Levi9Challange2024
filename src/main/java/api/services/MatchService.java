package api.services;

import api.models.CommandResponse;
import api.servicesInterface.MatchServiceI;
import api.servicesInterface.PlayerServiceI;
import org.springframework.stereotype.Service;

import java.net.http.HttpClient;

@Service
public class MatchService implements MatchServiceI {

    public MatchService() {
    }

    @Override
    public CommandResponse<Object> getMatches() {
        return null;
    }


}
