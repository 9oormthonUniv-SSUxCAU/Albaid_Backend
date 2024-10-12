package Albaid.backend.domain.card.application;

import Albaid.backend.domain.card.application.dto.AlbaCardListDTO;
import Albaid.backend.domain.card.application.dto.AlbaCardResponseDTO;
import Albaid.backend.domain.card.application.dto.AlbaCardUpdateDto;

public interface AlbaCardService {

    AlbaCardResponseDTO createAlbaCard(Integer contractId);

    AlbaCardListDTO getAlbaCardList();

    AlbaCardResponseDTO getAlbaCardById(Integer id);

    AlbaCardResponseDTO updateAlbaCard(Integer id, AlbaCardUpdateDto request);

    void deleteAlbaCard(Integer id);

    void endAlbaCard(Integer id);
}
