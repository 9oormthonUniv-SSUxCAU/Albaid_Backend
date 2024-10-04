package Albaid.backend.domain.contract.repository;

import Albaid.backend.domain.contract.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

import java.util.List;

public interface ContractRepository extends JpaRepository<Contract, Integer> {
    List<Contract> findByMemberId(Long memberId);


    @Query("SELECT c FROM Contract c WHERE c.member.id = :memberId")
    List<Contract> findContractsByMemberId(Integer memberId);
}
