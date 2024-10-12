package Albaid.backend.domain.card.api;

import Albaid.backend.domain.card.application.AlbaCardService;
import Albaid.backend.domain.card.application.dto.AlbaCardListDTO;
import Albaid.backend.domain.card.application.dto.AlbaCardResponseDTO;
import Albaid.backend.domain.card.application.dto.AlbaCardUpdateDto;
import Albaid.backend.global.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/card")
@RequiredArgsConstructor
public class AlbaCardController {

    private final AlbaCardService albaCardService;

    // 알바카드 추가
    @PostMapping
    public Response<AlbaCardResponseDTO> createAlbaCard(@RequestBody Integer contractId) {
        AlbaCardResponseDTO albaCard = albaCardService.createAlbaCard(contractId);
        return Response.success(albaCard);
    }

    @GetMapping
    public Response<AlbaCardListDTO> getAlbaCardList() {
        AlbaCardListDTO albaCardList = albaCardService.getAlbaCardList();
        return Response.success(albaCardList);
    }

    // 알바카드 조회
    @GetMapping("/{id}")
    public Response<AlbaCardResponseDTO> getAlbaCard(@PathVariable Integer id) {
        AlbaCardResponseDTO albaCard = albaCardService.getAlbaCardById(id);
        return Response.success(albaCard);
    }

    // 알바카드 수정
    @PatchMapping("/{id}")
    public Response<AlbaCardResponseDTO> updateAlbaCard(@PathVariable Integer id, @RequestBody AlbaCardUpdateDto request) {
        AlbaCardResponseDTO albaCard = albaCardService.updateAlbaCard(id, request);
        return Response.success(albaCard);
    }

    // 알바카드 삭제
    @DeleteMapping("/{id}")
    public Response<Void> deleteAlbaCard(@PathVariable Integer id) {
        albaCardService.deleteAlbaCard(id);
        return Response.success();
    }

    // 알바카드 종료
    @PatchMapping("/{id}/end")
    public Response<Void> endAlbaCard(@PathVariable Integer id) {
        albaCardService.endAlbaCard(id);
        return Response.success();
    }
}
