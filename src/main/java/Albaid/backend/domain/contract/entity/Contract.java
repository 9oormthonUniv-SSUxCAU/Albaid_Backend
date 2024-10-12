package Albaid.backend.domain.contract.entity;

import Albaid.backend.domain.card.entity.AlbaCard;
import Albaid.backend.domain.contract.application.dto.RequestContractDTO;
import Albaid.backend.domain.member.entity.Member;
import Albaid.backend.global.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Contract extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;
    private String url;
    private String workplace;
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;
    private LocalTime standardWorkingStartTime;
    private LocalTime standardWorkingEndTime;
    private int hourlyWage;
    private String jobDescription;
    private boolean isPaidAnnualLeave;
    private boolean isSocialInsurance;
    private boolean isContractDelivery;
    private String memo;
    private boolean isSafe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne(mappedBy = "contract")
    private AlbaCard albaCard;

    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkingDays> workingDays = new ArrayList<>();

    public void addWorkingDay(WorkingDays workingDay) {
        this.workingDays.add(workingDay);
        workingDay.setContract(this);
    }

    public void update(RequestContractDTO dto) {
        this.title = dto.title();
        this.workplace = dto.workplace();
        this.contractStartDate = dto.contractStartDate();
        this.contractEndDate = dto.contractEndDate();
        this.standardWorkingStartTime = dto.standardWorkingStartTime();
        this.standardWorkingEndTime = dto.standardWorkingEndTime();
        this.hourlyWage = dto.hourlyWage();
        this.jobDescription = dto.jobDescription();
        this.isPaidAnnualLeave = dto.isPaidAnnualLeave();
        this.isSocialInsurance = dto.isSocialInsurance();
        this.isContractDelivery = dto.isContractDelivery();
        this.memo = dto.memo();

        updateWorkingDays(dto.workingDays());
    }

    private void updateWorkingDays(List<String> newWorkingDays) {
        this.workingDays.clear();
        newWorkingDays.forEach(day -> this.addWorkingDay(new WorkingDays(day, this)));
    }
}

