package Albaid.backend.domain.resume.api;

import Albaid.backend.domain.resume.application.ResumeService;
import Albaid.backend.domain.resume.application.dto.CareerResponseDTO;
import Albaid.backend.domain.resume.application.dto.ResumeRequestDTO;
import Albaid.backend.domain.resume.application.dto.ResumeResponseDTO;
import Albaid.backend.domain.resume.application.dto.SummaryResumeDTO;
import Albaid.backend.global.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resume")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    // 이력서 생성하기
    @PostMapping
    public Response<ResumeResponseDTO> createResume(@RequestBody ResumeRequestDTO request) {
        ResumeResponseDTO createdResume = resumeService.createResume(request);
        return Response.success(createdResume);
    }

    // 이력서 목록 조회
    @GetMapping
    public Response<List<SummaryResumeDTO>> getResumeList() {
        List<SummaryResumeDTO> summaryResumeDTOS = resumeService.getResumeList();
        return Response.success(summaryResumeDTOS);
    }

    // 이력서 조회하기
    @GetMapping("/{id}")
    public Response<ResumeResponseDTO> getResume(@PathVariable Integer id) {
        ResumeResponseDTO resume = resumeService.getResumeById(id);
        return Response.success(resume);
    }

    // 이력서 수정하기
    @PutMapping("/{id}")
    public Response<ResumeResponseDTO> updateResume(@PathVariable Integer id, @RequestBody ResumeRequestDTO request) {
        ResumeResponseDTO updatedResume = resumeService.updateResume(id, request);
        return Response.success(updatedResume);
    }

    // 이력서 삭제하기
    @DeleteMapping("/{id}")
    public Response<Void> deleteResume(@PathVariable Integer id) {
        resumeService.deleteResume(id);
        return Response.success();
    }

    // 경력사항 불러오기
    @GetMapping("/{id}/careers")
    public Response<List<CareerResponseDTO>> getCareer(@PathVariable Integer id) {
        List<CareerResponseDTO> careers = resumeService.getCareer(id);
        return Response.success(careers);
    }
}