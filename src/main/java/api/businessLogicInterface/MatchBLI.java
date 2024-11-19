package api.businessLogicInterface;


import api.models.CommandResponse;

public interface MatchBLI {

    CommandResponse<Object> getMatches();
}
