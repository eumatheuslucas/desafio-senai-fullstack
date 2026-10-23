package br.gov.senai.passe.contract;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractRepository extends JpaRepository<Contract, Integer> {

    Optional<Contract> findByPlayerIdAndStatus(Integer playerId, ContractStatus status);

    List<Contract> findByClubIdAndStatus(Integer clubId, ContractStatus status);
}
