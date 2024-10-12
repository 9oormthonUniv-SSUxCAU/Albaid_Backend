package Albaid.backend.domain.card.repository;

import Albaid.backend.domain.card.entity.AlbaCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbaCardRepository extends JpaRepository<AlbaCard, Integer> {
}
