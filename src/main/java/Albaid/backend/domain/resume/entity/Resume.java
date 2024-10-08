package Albaid.backend.domain.resume.entity;

import Albaid.backend.domain.career.entity.Career;
import Albaid.backend.domain.member.entity.Member;
import Albaid.backend.global.base.BaseEntity;
import Albaid.backend.global.enums.EducationLevel;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Resume extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String summary;
    private String phone;
    private String email;
    private String address;

    @Enumerated(EnumType.STRING)
    private EducationLevel finalEducation;

    private String totalCareerDuration;
    private String qualifications;
    private String desiredLocation;
    private String desiredJob;

    // Member 테이블과의 외래 키 관계 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "resume")
    private List<Career> careers = new ArrayList<>();
}
