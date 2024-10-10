package Albaid.backend.domain.resume.entity;

import Albaid.backend.domain.member.entity.Member;
import Albaid.backend.global.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Resume extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;
    private String summary;
    private String phone;
    private String email;
    private String address;

    @Enumerated(EnumType.STRING)
    private EducationLevel finalEducation;

    private String qualifications;
    private String desiredLocation;
    private String desiredJob;

    // Member 테이블과의 외래 키 관계 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Career> careers = new ArrayList<>();

    public void setCareers(List<Career> careers) {
        this.careers = careers;
    }

    public void update(String title, String summary, String phone, String address, String email, EducationLevel finalEducation, String qualifications, String desiredLocation, String desiredJob) {
        this.title = title;
        this.summary = summary;
        this.phone = phone;
        this.address = address;
        this.email = email;
        this.finalEducation = finalEducation;
        this.qualifications = qualifications;
        this.desiredLocation = desiredLocation;
        this.desiredJob = desiredJob;
    }

}