package Albaid.backend.domain.contract.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class WorkingDays {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String workingDay;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id")
    private Contract contract;

    public WorkingDays(String workingDay, Contract contract) {
        this.workingDay = workingDay;
        this.contract = contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkingDays that = (WorkingDays) o;
        return Objects.equals(workingDay, that.workingDay) && Objects.equals(contract, that.contract);
    }

    @Override
    public int hashCode() {
        return Objects.hash(workingDay, contract);
    }
}

