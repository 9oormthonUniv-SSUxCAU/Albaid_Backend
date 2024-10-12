package Albaid.backend.domain.card.application;

import Albaid.backend.domain.card.application.dto.AlbaCardDTO;

public interface AlbaCardService {
    AlbaCardDTO getAlbaCardById(Integer id);
    AlbaCardDTO createAlbaCard(AlbaCardDTO albaCardDto);
    AlbaCardDTO updateAlbaCard(Integer id, AlbaCardDTO albaCardDto);
    void deleteAlbaCard(Integer id);
}
