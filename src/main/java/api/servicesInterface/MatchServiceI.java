package api.servicesInterface;


import api.modelsDTO.CreateMatchRequestDTO;
import api.modelsDTO.MatchResponseDTO;

import java.util.List;

public interface MatchServiceI {

    void createMatch(CreateMatchRequestDTO request);

    List<MatchResponseDTO> getAllMatches();

}
