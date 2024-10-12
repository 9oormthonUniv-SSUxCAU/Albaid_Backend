package Albaid.backend.domain.card.repository;

import Albaid.backend.domain.card.entity.AlbaCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbaCardRepository extends JpaRepository<AlbaCard, Integer> {

    List<AlbaCard> findAllByMemberId(Integer memberId);

    List<AlbaCard> findAllByMemberIdAndIsAliveTrueOrderByCreatedAtAsc(Integer memberId);
    List<AlbaCard> findAllByMemberIdAndIsAliveFalseOrderByCreatedAtAsc(Integer memberId);

    void deleteByContractId(Integer contractId);
}
