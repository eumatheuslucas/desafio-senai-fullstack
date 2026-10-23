package br.gov.senai.passe.match;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Match, Integer> {

    List<Match> findAllByOrderByPlayedAtDescIdDesc();

    List<Match> findByHomeClubIdOrAwayClubIdOrderByPlayedAtDescIdDesc(Integer homeClubId, Integer awayClubId);
}
