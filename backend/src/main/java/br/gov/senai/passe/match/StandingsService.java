package br.gov.senai.passe.match;

import br.gov.senai.passe.club.Club;
import br.gov.senai.passe.club.ClubRepository;
import br.gov.senai.passe.shared.util.TextNormalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StandingsService {

    private static final int WIN_POINTS = 3;
    private static final int DRAW_POINTS = 1;

    private final ClubRepository clubRepository;
    private final MatchRepository matchRepository;

    private static final class Tally {
        private final Club club;
        private int wins;
        private int draws;
        private int losses;
        private int goalsFor;
        private int goalsAgainst;

        private Tally(Club club) {
            this.club = club;
        }

        private void record(int scored, int conceded) {
            goalsFor += scored;
            goalsAgainst += conceded;
            if (scored > conceded) {
                wins++;
            } else if (scored == conceded) {
                draws++;
            } else {
                losses++;
            }
        }

        private int points() {
            return wins * WIN_POINTS + draws * DRAW_POINTS;
        }

        private int goalDifference() {
            return goalsFor - goalsAgainst;
        }
    }

    @Transactional(readOnly = true)
    public List<StandingRow> standings() {
        Map<Integer, Tally> tallies = new HashMap<>();
        clubRepository.findAll().forEach(club -> tallies.put(club.getId(), new Tally(club)));
        for (Match match : matchRepository.findAll()) {
            tallies.get(match.getHomeClubId()).record(match.getHomeGoals(), match.getAwayGoals());
            tallies.get(match.getAwayClubId()).record(match.getAwayGoals(), match.getHomeGoals());
        }
        Comparator<Tally> order = Comparator
                .comparingInt(Tally::points).reversed()
                .thenComparing(Comparator.comparingInt(Tally::goalDifference).reversed())
                .thenComparing(Comparator.comparingInt((Tally tally) -> tally.goalsFor).reversed())
                .thenComparing(tally -> TextNormalizer.fold(tally.club.getName()))
                .thenComparing(tally -> tally.club.getId());
        List<Tally> sorted = tallies.values().stream().sorted(order).toList();
        List<StandingRow> rows = new ArrayList<>();
        for (int index = 0; index < sorted.size(); index++) {
            rows.add(toRow(index + 1, sorted.get(index)));
        }
        return rows;
    }

    private StandingRow toRow(int position, Tally tally) {
        return new StandingRow(position, tally.club.getId(), tally.club.getName(),
                tally.wins + tally.draws + tally.losses, tally.wins, tally.draws, tally.losses,
                tally.goalsFor, tally.goalsAgainst, tally.goalDifference(), tally.points());
    }
}
