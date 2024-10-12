package Albaid.backend.domain.calendar.entity;

import Albaid.backend.domain.card.entity.AlbaCard;
import Albaid.backend.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "calendar")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Calendar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private LocalDate date;  // 근무 날짜

    @Column(nullable = false)
    private LocalTime startTime; // 근무 시작 시간

    @Column(nullable = false)
    private LocalTime endTime; // 근무 종료 시간

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alba_card_id")
    private AlbaCard albaCard; // 알바카드와 연관 관계


    public Calendar(AlbaCard albaCard, LocalDate date, LocalTime startTime, LocalTime endTime) {
        this.albaCard = albaCard;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
