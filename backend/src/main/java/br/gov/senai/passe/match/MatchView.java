package br.gov.senai.passe.match;

import br.gov.senai.passe.player.Condition;
import br.gov.senai.passe.player.Position;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

public record MatchView(
        Integer id,
        String playedAt,
        TeamResult home,
        TeamResult away,
        String result,
        @JsonInclude(JsonInclude.Include.NON_NULL) List<DuelView> duels) {

    public record TeamResult(Integer clubId, String clubName, String formation, int goals) {
    }

    public record DuelView(int order, Integer attackingClubId, DuelPlayer attacker, DuelPlayer defender,
                           boolean goal) {
    }

    public record DuelPlayer(Integer playerId, String playerName, Position position, Condition condition,
                             String attribute, int value) {
    }

    public static String resultOf(int homeGoals, int awayGoals) {
        if (homeGoals > awayGoals) {
            return "MANDANTE";
        }
        return homeGoals < awayGoals ? "VISITANTE" : "EMPATE";
    }
}
