package Albaid.backend.domain.contract.entity;

import Albaid.backend.domain.member.entity.Member;
import Albaid.backend.global.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Contract extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String url;
    private String workplace;
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;
    private LocalTime standardWorkingStartTime;
    private LocalTime standardWorkingEndTime;
    private int hourlyWage;  // 시급
    private String jobDescription;  // 업무내용
    private boolean isPaidAnnualLeave;  // 연차유급휴가내용
    private boolean isSocialInsurance;  // 사회보험적용
    private boolean isContractDelivery;  // 근로계약서 교부
    private String memo;  // 메모

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;  // 회원식별자

    @OneToMany(mappedBy = "contract")
    private List<WorkingDays> workingDays;
}
