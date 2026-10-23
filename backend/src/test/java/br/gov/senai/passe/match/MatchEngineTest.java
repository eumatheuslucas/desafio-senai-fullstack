package br.gov.senai.passe.match;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.gov.senai.passe.player.Condition;
import br.gov.senai.passe.player.Player;
import br.gov.senai.passe.player.Position;
import java.util.List;
import org.junit.jupiter.api.Test;

class MatchEngineTest {

    private static Player player(int id, Position position, Condition condition, int shooting, int passing,
                                 int dribbling, int defending) {
        Player player = new Player();
        player.setId(id);
        player.setName("Jogador " + id);
        player.setPosition(position);
        player.setCondition(condition);
        player.setShooting(shooting);
        player.setPassing(passing);
        player.setDribbling(dribbling);
        player.setDefending(defending);
        return player;
    }

    @Test
    void reproducesTheWorkedExampleOfTheRules() {
        List<Player> home = List.of(
                player(30, Position.GOL, Condition.APTO, 0, 0, 0, 77),
                player(32, Position.ZAG, Condition.APTO, 0, 0, 0, 84),
                player(31, Position.LAT, Condition.MAL_CONDICIONADO, 0, 0, 74, 66),
                player(33, Position.MEI, Condition.APTO, 0, 82, 0, 0),
                player(34, Position.ATA, Condition.APTO, 75, 0, 0, 0));
        List<Player> away = List.of(
                player(40, Position.GOL, Condition.APTO, 0, 0, 0, 72),
                player(41, Position.ZAG, Condition.APTO, 0, 0, 0, 84),
                player(42, Position.LAT, Condition.APTO, 0, 0, 79, 63),
                player(43, Position.MEI, Condition.APTO, 0, 70, 0, 0),
                player(44, Position.ATA, Condition.APTO, 81, 0, 0, 0));

        List<MatchEngine.Duel> duels = MatchEngine.play(home, away);

        assertEquals(6, duels.size());
        assertTrue(duels.get(0).goal());
        assertFalse(duels.get(1).goal());
        assertFalse(duels.get(2).goal());
        assertEquals(63, duels.get(2).attackValue());
        assertEquals(63, duels.get(2).defenseValue());
        assertTrue(duels.get(3).goal());
        assertFalse(duels.get(4).goal());
        assertTrue(duels.get(5).goal());
        assertEquals(56, duels.get(5).defenseValue());
        assertEquals(1, duels.stream().filter(duel -> duel.homeAttacking() && duel.goal()).count());
        assertEquals(2, duels.stream().filter(duel -> !duel.homeAttacking() && duel.goal()).count());
    }

    @Test
    void conditionPenaltyRoundsHalfUp() {
        Player sick = player(1, Position.LAT, Condition.MAL_CONDICIONADO, 0, 0, 0, 0);
        assertEquals(63, MatchEngine.effective(sick, 74));
        assertEquals(43, MatchEngine.effective(sick, 50));
        assertEquals(56, MatchEngine.effective(sick, 66));
    }
}
