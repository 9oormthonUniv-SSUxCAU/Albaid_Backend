package Albaid.backend.domain.card.api;

import Albaid.backend.domain.card.application.AlbaCardService;
import Albaid.backend.domain.card.application.dto.AlbaCardDTO;
import Albaid.backend.global.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/albaCard")
@RequiredArgsConstructor
public class AlbaCardController {

    private final AlbaCardService albaCardService;

    // 알바카드 추가
    @PostMapping
    public Response<AlbaCardDTO> createAlbaCard(@RequestBody AlbaCardDTO albaCardDto) {
        AlbaCardDTO createdAlbaCard = albaCardService.createAlbaCard(albaCardDto);
        return Response.success(createdAlbaCard);
    }

//    // 알바카드 조회
//    @GetMapping("/{id}")
//    public Response<AlbaCardDTO> getAlbaCard(@PathVariable Long id) {
//        AlbaCardDTO albaCard = albaCardService.getAlbaCardById(id);
//        return Response.success(albaCard);
//    }
//
//    // 알바카드 수정
//    @PutMapping("/{id}")
//    public Response<AlbaCardDTO> updateAlbaCard(@PathVariable Long id, @RequestBody AlbaCardDTO albaCardDto) {
//        AlbaCardDTO updatedAlbaCard = albaCardService.updateAlbaCard(id, albaCardDto);
//        return Response.success(updatedAlbaCard);
//    }
//
//    // 알바카드 삭제
//    @DeleteMapping("/{id}")
//    public Response<Void> deleteAlbaCard(@PathVariable Long id) {
//        albaCardService.deleteAlbaCard(id);
//        return Response.success();
//    }
}
