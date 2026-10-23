package br.gov.senai.passe.market;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<MarketTransaction, Integer> {

    List<MarketTransaction> findByClubIdOrderByCreatedAtDescIdDesc(Integer clubId);

    boolean existsByClubIdAndPlayerIdAndType(Integer clubId, Integer playerId, TransactionType type);
}
