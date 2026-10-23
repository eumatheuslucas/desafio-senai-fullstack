package br.gov.senai.passe.lineup;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LineupRepository extends JpaRepository<LineupSlot, LineupSlotId> {

    List<LineupSlot> findByClubId(Integer clubId);

    @Modifying(flushAutomatically = true)
    @Query("delete from LineupSlot s where s.clubId = :clubId")
    void deleteAllByClub(@Param("clubId") Integer clubId);

    @Modifying(flushAutomatically = true)
    @Query("delete from LineupSlot s where s.playerId = :playerId")
    void deleteByPlayer(@Param("playerId") Integer playerId);
}
