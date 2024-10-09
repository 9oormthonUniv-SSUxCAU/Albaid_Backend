package Albaid.backend.domain.resume.application;

import Albaid.backend.domain.Career.application.dto.CareerDTO;
import Albaid.backend.domain.Career.entity.Career;
import Albaid.backend.domain.contract.application.ContractService;
import Albaid.backend.domain.contract.application.dto.ContractDTO;
import Albaid.backend.domain.resume.application.dto.ResumeDTO;
import Albaid.backend.domain.resume.entity.Resume;
import Albaid.backend.domain.resume.repository.ResumeRepository;
import Albaid.backend.global.response.CustomException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static Albaid.backend.global.response.ErrorCode.NOT_FOUND_RESOURCE;

@Service
@Transactional
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {

    private final ResumeRepository resumeRepository;
    private final ContractService contractService;

    @Override
    public ResumeDTO getResumeById(Long id) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new CustomException(NOT_FOUND_RESOURCE, "이력서를 찾을 수 없습니다."));

        // 근로계약서에서 추출된 경력사항 가져오기
        List<ContractDTO> contracts = contractService.getContractsForMember(resume.getMember().getId());

        // 경력사항 추가
        // 기존의 경력 + 근로계약서에서 추출된 경력 결합
        List<CareerDTO> combinedCareers = new ArrayList<>();
        // combinedCareers.addAll(convertCareersToDTO(resume.getCareers()));  // 이력서에 추가된 경력 주석 처리
        combinedCareers.addAll(convertContractsToCareers(contracts));  // 계약서에서 추출된 경력

        // 최종적으로 결합된 경력을 포함한 ResumeDTO 반환
        return ResumeDTO.of(resume, combinedCareers);
    }

    @Transactional
    @Override
    public ResumeDTO createResume(ResumeDTO resumeDto) {
        Resume resume = Resume.builder()
                .title(resumeDto.getTitle())
                .summary(resumeDto.getSummary())
                .phone(resumeDto.getPhone())
                .address(resumeDto.getAddress())
                .email(resumeDto.getEmail())
                .finalEducation(resumeDto.getFinalEducation())
                .build();

        resumeRepository.save(resume);

        // 계약서 정보도 함께 처리
        List<ContractDTO> contracts = contractService.getContractsForMember(resumeDto.getMemberId());
        List<CareerDTO> careersFromContracts = convertContractsToCareers(contracts);

        return ResumeDTO.of(resume, careersFromContracts);
    }

    @Override
    public String generateShareLink(Long id) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new CustomException(NOT_FOUND_RESOURCE, "이력서를 찾을 수 없습니다."));

        // 공유 링크 생성
        String shareLink = "http://example.com/share/resume/" + id;
        return shareLink;
    }

    @Override
    public ResumeDTO updateResume(Long id, ResumeDTO resumeDto) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new CustomException(NOT_FOUND_RESOURCE, "이력서를 찾을 수 없습니다."));

        // 전달받은 DTO 값을 기존 이력서에 업데이트
        resume.setTitle(resumeDto.getTitle());
        resume.setSummary(resumeDto.getSummary());
        resume.setPhone(resumeDto.getPhone());
        resume.setAddress(resumeDto.getAddress());
        resume.setEmail(resumeDto.getEmail());
        resume.setFinalEducation(resumeDto.getFinalEducation());
        resume.setDesiredLocation(resumeDto.getDesiredLocation());
        resume.setDesiredJob(resumeDto.getDesiredJob());
        resume.setTotalCareerDuration(resumeDto.getTotalCareerDuration());

        resumeRepository.save(resume);

        // 계약서 정보도 함께 처리
        List<ContractDTO> contracts = contractService.getContractsForMember(resumeDto.getMemberId());
        List<CareerDTO> careersFromContracts = convertContractsToCareers(contracts);

        return ResumeDTO.of(resume, careersFromContracts);
    }

    @Override
    public ResumeDTO addCareer(Long id, CareerDTO careerDto) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new CustomException(NOT_FOUND_RESOURCE, "이력서를 찾을 수 없습니다."));

        // 새로운 경력 생성 및 추가
        Career career = Career.builder()
                .companyName(careerDto.getCompanyName())
                .startDate(careerDto.getStartDate())
                .endDate(careerDto.getEndDate())
                .resume(resume)
                .build();

        resume.getCareers().add(career); // 이력서에 새 경력 추가
        resumeRepository.save(resume); // 이력서 저장

        List<ContractDTO> contracts = contractService.getContractsForMember(resume.getMember().getId());

        // 기존의 경력 + 근로계약서에서 추출된 경력 결합
        List<CareerDTO> combinedCareers = new ArrayList<>();
        // combinedCareers.addAll(convertCareersToDTO(resume.getCareers()));  // 이력서에 추가된 경력 주석 처리
        combinedCareers.addAll(convertContractsToCareers(contracts));  // 계약서에서 추출된 경력

        // 최종적으로 결합된 경력을 포함한 ResumeDTO 반환
        return ResumeDTO.of(resume, combinedCareers);
    }

    @Override
    public void deleteResume(Long id) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new CustomException(NOT_FOUND_RESOURCE, "이력서를 찾을 수 없습니다."));

        resumeRepository.delete(resume);
    }

    private List<CareerDTO> convertContractsToCareers(List<ContractDTO> contracts) {
        return contracts.stream()
                .map(contract -> {

                    LocalDate startDate = contract.getContractStartDate();
                    LocalDate endDate = contract.getContractEndDate();

                    // 두 날짜 간의 개월 수 계산
                    long totalMonths = ChronoUnit.MONTHS.between(startDate, endDate);

                    return CareerDTO.builder()
                            .companyName(contract.getWorkplace())
                            .startDate(startDate)
                            .endDate(endDate)
                            .build();
                })
                .collect(Collectors.toList());
    }

}
