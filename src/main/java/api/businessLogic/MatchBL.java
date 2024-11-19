package api.businessLogic;

import api.businessLogicInterface.MatchBLI;
import api.models.CommandResponse;
import api.servicesInterface.MatchServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MatchBL implements MatchBLI {

    private final MatchServiceI _service;

    @Autowired
    public MatchBL(MatchServiceI service) {
        this._service = service;
    }


    @Override
    public CommandResponse<Object> getMatches() {
        return null;
    }
}
