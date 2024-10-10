package Albaid.backend.domain.resume.application.dto;

import Albaid.backend.domain.resume.entity.Resume;

import java.time.format.DateTimeFormatter;

public record SummaryResumeDTO(Integer id, String createdAt, String title, String desiredJob,
                               String desiredLocation) {
    public static SummaryResumeDTO of(Resume resume) {
        return new SummaryResumeDTO(resume.getId(), resume.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")), resume.getTitle(), resume.getDesiredJob(), resume.getDesiredLocation());
    }
}