package Albaid.backend.domain.contract.repository;

import Albaid.backend.domain.contract.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContractRepository extends JpaRepository<Contract, Integer> {
    List<Contract> findByMemberId(Long memberId);
}
