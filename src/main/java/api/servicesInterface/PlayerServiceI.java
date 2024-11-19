package api.servicesInterface;


import api.models.CommandResponse;

public interface PlayerServiceI {
    CommandResponse<Object> create();

    CommandResponse<Object> getPlayerById();
}
