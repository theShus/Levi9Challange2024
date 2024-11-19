package api.businessLogic;

import api.businessLogicInterface.PlayerBLI;
import api.models.CommandResponse;
import api.servicesInterface.PlayerServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayerBL implements PlayerBLI {

    private final PlayerServiceI _service;

    @Autowired
    public PlayerBL(PlayerServiceI service) {
        this._service = service;
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
