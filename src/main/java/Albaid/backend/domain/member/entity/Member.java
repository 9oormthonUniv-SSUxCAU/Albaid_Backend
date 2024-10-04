package Albaid.backend.domain.member.entity;

import Albaid.backend.domain.contract.entity.Contract;
import Albaid.backend.global.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;  // 이름
    private LocalDate birthdate;  // 생년월일
    private String phone;  // 전화번호
    private String provider; // 소셜 로그인 제공자
    private String providerId; // 소셜 로그인 제공자 ID

    @OneToMany(mappedBy = "member")
    private List<Contract> contracts = new ArrayList<>();
}
