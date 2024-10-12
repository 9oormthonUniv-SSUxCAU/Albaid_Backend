package Albaid.backend.domain.card.entity;

import Albaid.backend.domain.contract.entity.Contract;
import Albaid.backend.domain.member.entity.Member;
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
    private String memo;
    private boolean isAlive;

    @OneToOne
    @JoinColumn(name = "contract_id")
    private Contract contract;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

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
