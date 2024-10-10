package Albaid.backend.domain.resume.application;

import Albaid.backend.domain.member.application.MemberService;
import Albaid.backend.domain.member.entity.Member;
import Albaid.backend.domain.resume.application.dto.*;
import Albaid.backend.domain.resume.entity.Career;
import Albaid.backend.domain.resume.entity.Resume;
import Albaid.backend.domain.resume.repository.CareerRepository;
import Albaid.backend.domain.resume.repository.ResumeRepository;
import Albaid.backend.global.response.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static Albaid.backend.global.response.ErrorCode.NOT_FOUND_RESOURCE;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {

    private final ResumeRepository resumeRepository;
    private final CareerRepository careerRepository;

    private final MemberService memberService;

    @Transactional
    @Override
    public ResumeResponseDTO createResume(ResumeRequestDTO request) {

        Member member = memberService.getCurrentMember();

        Resume resume = ResumeRequestDTO.toEntity(request, member);
        List<Career> careers = resume.getCareers();
        for (Career career : careers) {
            career.setResume(resume);
        }

        Resume savedResume = resumeRepository.save(resume);

        return ResumeResponseDTO.of(savedResume);
    }

    @Override
    public List<SummaryResumeDTO> getResumeList() {
        Member member = memberService.getCurrentMember();
        List<Resume> resumes = resumeRepository.findAllByMemberOrderByCreatedAtDesc(member);
        return resumes.stream().map(SummaryResumeDTO::of).toList();
    }

    @Override
    public ResumeResponseDTO getResumeById(Integer id) {
        Resume resume = resumeRepository.findById(id).orElseThrow(() -> new CustomException(NOT_FOUND_RESOURCE, "이력서를 찾을 수 없습니다."));
        List<Career> sortedCareer = careerRepository.findByResumeOrderByStartDateDesc(resume);
        resume.setCareers(sortedCareer);
        return ResumeResponseDTO.of(resume);
    }

    @Transactional
    @Override
    public ResumeResponseDTO updateResume(Integer id, ResumeRequestDTO request) {

        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new CustomException(NOT_FOUND_RESOURCE, "이력서를 찾을 수 없습니다."));

        // 이력서 정보 업데이트
        resume.update(
                request.title(),
                request.summary(),
                request.phone(),
                request.address(),
                request.email(),
                request.finalEducation(),
                request.qualifications(),
                request.desiredLocation(),
                request.desiredJob()
        );

        // 기존 경력 삭제 및 새로운 경력 추가
        resume.getCareers().clear();

        // 새로운 Career 엔티티 리스트 생성
        List<Career> newCareers = request.careers().stream()
                .map(careerDTO -> {
                    Career newCareer = CareerRequestDTO.toEntity(careerDTO);
                    newCareer.setResume(resume);
                    return newCareer;
                })
                .collect(Collectors.toList());

        // 새로운 경력사항들을 한 번에 저장
        List<Career> savedCareers = careerRepository.saveAll(newCareers); // saveAll 사용

        resume.getCareers().addAll(savedCareers);

        return ResumeResponseDTO.of(resume);
    }

    @Transactional
    @Override
    public void deleteResume(Integer id) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new CustomException(NOT_FOUND_RESOURCE, "이력서를 찾을 수 없습니다."));

        resumeRepository.delete(resume);
    }

    @Override
    public List<CareerResponseDTO> getCareer(Integer id) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new CustomException(NOT_FOUND_RESOURCE, "이력서를 찾을 수 없습니다."));

        List<Career> careers = careerRepository.findByResumeOrderByStartDateDesc(resume);

        return careers.stream()
                .map(CareerResponseDTO::of)
                .collect(Collectors.toList());
    }
}