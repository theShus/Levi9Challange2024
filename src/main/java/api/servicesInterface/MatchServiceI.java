package api.servicesInterface;


import api.models.CommandResponse;

public interface MatchServiceI {
    CommandResponse<Object> getMatches();
}
