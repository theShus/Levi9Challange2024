package api.servicesInterface;


import api.models.CommandResponse;

public interface TeamServiceI {
    CommandResponse<Object> getTeams();
    CommandResponse<Object> getTeamById();

}
