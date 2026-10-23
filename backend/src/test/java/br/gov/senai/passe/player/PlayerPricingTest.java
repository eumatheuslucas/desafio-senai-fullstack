package br.gov.senai.passe.player;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class PlayerPricingTest {

    private static void assertMarket(String base, Condition condition, int agility, int age, String expected) {
        BigDecimal value = PlayerPricing.marketValue(new BigDecimal(base), condition, agility, age);
        assertEquals(new BigDecimal(expected), value);
    }

    private static Player player(Position position, int pace, int shooting, int passing, int dribbling,
                                 int defending, int physical) {
        Player player = new Player();
        player.setPosition(position);
        player.setPace(pace);
        player.setShooting(shooting);
        player.setPassing(passing);
        player.setDribbling(dribbling);
        player.setDefending(defending);
        player.setPhysical(physical);
        return player;
    }

    @Test
    void marketValueFollowsConditionAgilityAndAgeRules() {
        assertMarket("82.00", Condition.APTO, 20, 25, "82.00");
        assertMarket("82.00", Condition.APTO, 50, 19, "90.20");
        assertMarket("82.00", Condition.MAL_CONDICIONADO, 50, 25, "69.70");
        assertMarket("82.00", Condition.MAL_CONDICIONADO, 80, 21, "84.05");
        assertMarket("82.00", Condition.MAL_CONDICIONADO, 30, 33, "53.30");
        assertMarket("82.00", Condition.DOENTE, 90, 25, "69.70");
        assertMarket("82.00", Condition.DOENTE, 30, 25, "49.20");
        assertMarket("82.00", Condition.DOENTE, 10, 40, "49.20");
        assertMarket("9.50", Condition.MAL_CONDICIONADO, 85, 25, "8.79");
        assertMarket("1.00", Condition.MAL_CONDICIONADO, 50, 35, "1.00");
    }

    @Test
    void overallUsesPositionWeightsRoundingHalfUp() {
        assertEquals(73, PlayerPricing.overall(player(Position.MEI, 68, 65, 80, 78, 52, 60)));
        assertEquals(76, PlayerPricing.overall(player(Position.ATA, 78, 75, 73, 82, 48, 68)));
        assertEquals(72, PlayerPricing.overall(player(Position.LAT, 72, 70, 78, 76, 70, 66)));
        assertEquals(73, PlayerPricing.overall(player(Position.ZAG, 50, 90, 60, 40, 80, 75)));
        assertEquals(68, PlayerPricing.overall(player(Position.GOL, 55, 30, 65, 40, 72, 68)));
    }

    @Test
    void baseValueUsesProgressiveBandsAndYoungPromise() {
        assertEquals(new BigDecimal("40.00"), PlayerPricing.baseValue(75, 86, 25));
        assertEquals(new BigDecimal("10.00"), PlayerPricing.baseValue(60, 70, 30));
        assertEquals(new BigDecimal("12.00"), PlayerPricing.baseValue(61, 70, 30));
        assertEquals(new BigDecimal("46.00"), PlayerPricing.baseValue(76, 80, 22));
        assertEquals(new BigDecimal("100.00"), PlayerPricing.baseValue(85, 92, 27));
        assertEquals(new BigDecimal("34.20"), PlayerPricing.baseValue(70, 84, 21));
        assertEquals(new BigDecimal("30.00"), PlayerPricing.baseValue(70, 84, 22));
        assertEquals(new BigDecimal("3.63"), PlayerPricing.baseValue(45, 90, 18));
        assertEquals(new BigDecimal("1.00"), PlayerPricing.baseValue(40, 66, 36));
    }
}
