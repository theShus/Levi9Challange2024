package api.businessLogicInterface;

import api.models.CommandResponse;

public interface TeamBLI {

    CommandResponse<Object> getTeams();

    CommandResponse<Object> getTeamById();
}
