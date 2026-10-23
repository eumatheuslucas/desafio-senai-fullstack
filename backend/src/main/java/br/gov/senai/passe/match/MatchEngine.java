package br.gov.senai.passe.match;

import br.gov.senai.passe.player.Condition;
import br.gov.senai.passe.player.Player;
import java.util.ArrayList;
import java.util.List;

public final class MatchEngine {

    private static final int LINE_START = 1;
    private static final int LINE_END = 5;

    private MatchEngine() {
    }

    public record Duel(
            int order,
            boolean homeAttacking,
            Player attacker,
            Player defender,
            String attackAttribute,
            int attackValue,
            String defenseAttribute,
            int defenseValue) {

        public boolean goal() {
            return attackValue > defenseValue;
        }
    }

    public static List<Duel> play(List<Player> home, List<Player> away) {
        List<Duel> duels = new ArrayList<>();
        addAttacks(duels, 1, true, home, away);
        addAttacks(duels, 4, false, away, home);
        return duels;
    }

    public static int effective(Player player, int rawValue) {
        if (player.getCondition() == Condition.MAL_CONDICIONADO) {
            return (rawValue * 85 + 50) / 100;
        }
        return rawValue;
    }

    private static void addAttacks(List<Duel> duels, int firstOrder, boolean homeAttacking,
                                   List<Player> attacking, List<Player> defending) {
        List<Player> attackLine = attacking.subList(LINE_START, LINE_END);
        List<Player> defenseLine = defending.subList(LINE_START, LINE_END);
        duels.add(duel(firstOrder, homeAttacking, attackLine.get(3), defending.get(0)));
        duels.add(duel(firstOrder + 1, homeAttacking, attackLine.get(2), defenseLine.get(0)));
        duels.add(duel(firstOrder + 2, homeAttacking, attackLine.get(1), defenseLine.get(1)));
    }

    private static Duel duel(int order, boolean homeAttacking, Player attacker, Player defender) {
        String attackAttribute = attackAttributeOf(attacker);
        return new Duel(order, homeAttacking, attacker, defender,
                attackAttribute, effective(attacker, valueOf(attacker, attackAttribute)),
                "defending", effective(defender, defender.getDefending()));
    }

    private static String attackAttributeOf(Player attacker) {
        return switch (attacker.getPosition()) {
            case ATA -> "shooting";
            case MEI -> "passing";
            case ZAG, LAT -> "dribbling";
            case GOL -> throw new IllegalStateException("Goleiro não ataca.");
        };
    }

    private static int valueOf(Player player, String attribute) {
        return switch (attribute) {
            case "shooting" -> player.getShooting();
            case "passing" -> player.getPassing();
            default -> player.getDribbling();
        };
    }
}
