package br.gov.senai.passe.market;

import br.gov.senai.passe.shared.util.Money;
import java.math.BigDecimal;

public final class MarketRules {

    private static final BigDecimal FEE_THRESHOLD = new BigDecimal("30.00");
    private static final BigDecimal FEE_RATE = new BigDecimal("0.05");
    private static final BigDecimal TEN = new BigDecimal("10");
    private static final BigDecimal LOW_RATE = new BigDecimal("0.10");
    private static final BigDecimal HIGH_RATE = new BigDecimal("0.20");

    private MarketRules() {
    }

    public static BigDecimal purchaseFee(BigDecimal marketValue) {
        if (marketValue.compareTo(FEE_THRESHOLD) <= 0) {
            return Money.ZERO;
        }
        return Money.round(marketValue.multiply(FEE_RATE));
    }

    public static boolean withinCashLimit(BigDecimal totalCost, BigDecimal cashBefore) {
        return totalCost.multiply(BigDecimal.valueOf(2)).compareTo(cashBefore) <= 0;
    }

    public static BigDecimal commission(BigDecimal profit) {
        if (profit.signum() <= 0) {
            return Money.ZERO;
        }
        BigDecimal lowPart = profit.min(TEN).multiply(LOW_RATE);
        BigDecimal highPart = profit.subtract(TEN).max(BigDecimal.ZERO).multiply(HIGH_RATE);
        return Money.round(lowPart.add(highPart));
    }
}
