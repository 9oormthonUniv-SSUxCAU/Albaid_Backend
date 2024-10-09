package Albaid.backend.domain.card.application;

import Albaid.backend.domain.card.application.dto.AlbaCardDTO;

public interface AlbaCardService {
    AlbaCardDTO getAlbaCardById(Long id);
    AlbaCardDTO createAlbaCard(AlbaCardDTO albaCardDto);
    AlbaCardDTO updateAlbaCard(Long id, AlbaCardDTO albaCardDto);
    void deleteAlbaCard(Long id);
}
