package api.businessLogicInterface;

import api.models.CommandResponse;

public interface PlayerBLI {

    CommandResponse<Object> create();

    CommandResponse<Object> getPlayerById();
}
