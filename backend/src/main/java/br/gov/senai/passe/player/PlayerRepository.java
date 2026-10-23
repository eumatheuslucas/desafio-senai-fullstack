package br.gov.senai.passe.player;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlayerRepository extends JpaRepository<Player, Integer> {

    Optional<Player> findByIdAndDeletedAtIsNull(Integer id);

    List<Player> findByDeletedAtIsNull();

    List<Player> findByClubIdAndDeletedAtIsNull(Integer clubId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Player p where p.id = :id and p.deletedAt is null")
    Optional<Player> findActiveForUpdate(@Param("id") Integer id);
}
