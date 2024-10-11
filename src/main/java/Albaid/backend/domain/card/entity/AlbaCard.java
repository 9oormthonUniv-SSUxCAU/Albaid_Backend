package Albaid.backend.domain.card.entity;

import Albaid.backend.domain.contract.entity.Contract;
import Albaid.backend.global.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AlbaCard extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private int totalWorkedHoursThisMonth;
    private int totalWageThisMonth;
    private int totalWorkedHours;
    private int totalWorkDays;
    private int totalWage;
    private String memo;

    @OneToOne
    @JoinColumn(name = "contract_id")
    private Contract contract;

    public String getWorkplace() {
        return this.contract.getWorkplace();
    }

    public String getContractStartDate() {
        return this.contract.getContractStartDate().toString();
    }

    public LocalDate getContractEndDate() {
        return this.contract.getContractEndDate();
    }
}
