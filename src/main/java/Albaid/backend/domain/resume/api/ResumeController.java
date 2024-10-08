package Albaid.backend.domain.resume.api;

import Albaid.backend.domain.career.application.dto.CareerDTO;
import Albaid.backend.domain.resume.application.ResumeService;
import Albaid.backend.domain.resume.application.dto.ResumeDTO;
import Albaid.backend.global.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resume")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    // 이력서 조회하기
    @GetMapping("/{id}")
    public Response<ResumeDTO> getResume(@PathVariable Long id) {
        ResumeDTO resume = resumeService.getResumeById(id);
        return Response.success(resume);
    }

    // 이력서 생성하기
    @PostMapping
    public Response<ResumeDTO> createResume(@RequestBody ResumeDTO resumeDto) {
        ResumeDTO createdResume = resumeService.createResume(resumeDto);
        return Response.success(createdResume);
    }

    // 이력서 공유하기
    @GetMapping("/{id}/share")
    public Response<String> shareResume(@PathVariable Long id) {
        String shareLink = resumeService.generateShareLink(id);
        return Response.success(shareLink);
    }


    // 이력서 수정하기
    @PutMapping("/{id}")
    public Response<ResumeDTO> updateResume(@PathVariable Long id, @RequestBody ResumeDTO resumeDto) {
        ResumeDTO updatedResume = resumeService.updateResume(id, resumeDto);
        return Response.success(updatedResume);
    }


    // 이략서 삭제하기
    @DeleteMapping("/{id}")
    public Response<Void> deleteResume(@PathVariable Long id) {
        resumeService.deleteResume(id);
        return Response.success();
    }

    // 경력 추가하기
    @PostMapping("/{id}/career")
    public Response<ResumeDTO> addCareer(@PathVariable Long id, @RequestBody CareerDTO careerDto) {
        ResumeDTO addedCareer = resumeService.addCareer(id, careerDto);
        return Response.success(addedCareer);
    }
}
