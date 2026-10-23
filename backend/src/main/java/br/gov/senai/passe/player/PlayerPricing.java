package br.gov.senai.passe.player;

import br.gov.senai.passe.shared.util.Money;
import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

public final class PlayerPricing {

    private static final BigDecimal HUNDRED = new BigDecimal("100");
    private static final BigDecimal TEN = new BigDecimal("10");
    private static final BigDecimal TWO = new BigDecimal("2");
    private static final BigDecimal MAX_DISCOUNT = new BigDecimal("-40");
    private static final BigDecimal MIN_VALUE = new BigDecimal("1.00");
    private static final BigDecimal FIRST_BAND_RATE = new BigDecimal("0.50");
    private static final BigDecimal SECOND_BAND_RATE = new BigDecimal("2.00");
    private static final BigDecimal THIRD_BAND_RATE = new BigDecimal("6.00");
    private static final int YOUNG_PROMISE_MAX_AGE = 21;

    private static final Map<Position, int[]> WEIGHTS = new EnumMap<>(Position.class);

    static {
        WEIGHTS.put(Position.GOL, new int[]{10, 0, 20, 0, 40, 30});
        WEIGHTS.put(Position.ZAG, new int[]{10, 0, 15, 0, 45, 30});
        WEIGHTS.put(Position.LAT, new int[]{25, 0, 20, 15, 25, 15});
        WEIGHTS.put(Position.MEI, new int[]{10, 15, 35, 25, 5, 10});
        WEIGHTS.put(Position.ATA, new int[]{20, 40, 10, 20, 0, 10});
    }

    private PlayerPricing() {
    }

    public static int overall(Player player) {
        int[] weight = WEIGHTS.get(player.getPosition());
        int sum = player.getPace() * weight[0]
                + player.getShooting() * weight[1]
                + player.getPassing() * weight[2]
                + player.getDribbling() * weight[3]
                + player.getDefending() * weight[4]
                + player.getPhysical() * weight[5];
        return (sum + 50) / 100;
    }

    public static BigDecimal baseValue(int overall, int potential, int ageAtRegistration) {
        BigDecimal part = band(overall, 40, 60, FIRST_BAND_RATE)
                .add(band(overall, 60, 75, SECOND_BAND_RATE))
                .add(band(overall, 75, 99, THIRD_BAND_RATE));
        if (ageAtRegistration <= YOUNG_PROMISE_MAX_AGE) {
            part = part.multiply(BigDecimal.valueOf(100L + potential - overall)).movePointLeft(2);
        }
        BigDecimal value = Money.round(part);
        return value.compareTo(MIN_VALUE) < 0 ? MIN_VALUE : value;
    }

    public static BigDecimal marketValue(Player player) {
        return marketValue(player.getBaseValue(), player.getCondition(), player.getAgility(), player.getAge());
    }

    public static BigDecimal marketValue(BigDecimal baseValue, Condition condition, int agility, int age) {
        BigDecimal total = ageAdjustment(age).subtract(conditionDiscount(condition, agility));
        if (total.compareTo(MAX_DISCOUNT) < 0) {
            total = MAX_DISCOUNT;
        }
        BigDecimal value = Money.round(baseValue.multiply(HUNDRED.add(total)).movePointLeft(2));
        return value.compareTo(MIN_VALUE) < 0 ? MIN_VALUE : value;
    }

    private static BigDecimal band(int overall, int lowerExclusive, int upperInclusive, BigDecimal ratePerPoint) {
        int points = Math.max(0, Math.min(overall, upperInclusive) - lowerExclusive);
        return BigDecimal.valueOf(points).multiply(ratePerPoint);
    }

    private static BigDecimal conditionDiscount(Condition condition, int agility) {
        if (condition == Condition.APTO) {
            return BigDecimal.ZERO;
        }
        BigDecimal base = condition == Condition.MAL_CONDICIONADO ? new BigDecimal("15") : new BigDecimal("30");
        if (agility >= 80) {
            return base.divide(TWO);
        }
        if (agility <= 30) {
            return base.add(TEN);
        }
        return base;
    }

    private static BigDecimal ageAdjustment(int age) {
        if (age <= 21) {
            return TEN;
        }
        if (age >= 32) {
            return TEN.negate();
        }
        return BigDecimal.ZERO;
    }
}
