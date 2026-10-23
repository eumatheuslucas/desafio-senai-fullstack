package br.gov.senai.passe.contract;

import br.gov.senai.passe.shared.util.Money;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

public final class ContractTerms {

    private static final long MILLIS_PER_DAY = 86_400_000L;
    private static final BigDecimal MAX_PENALTY = new BigDecimal("10.00");

    private ContractTerms() {
    }

    public static int remainingDays(LocalDateTime loyaltyUntil, LocalDateTime now) {
        long millis = Duration.between(now, loyaltyUntil).toMillis();
        if (millis <= 0) {
            return 0;
        }
        return (int) ((millis + MILLIS_PER_DAY - 1) / MILLIS_PER_DAY);
    }

    public static int penaltyRate(int remainingDays) {
        if (remainingDays == 0) {
            return 0;
        }
        if (remainingDays <= 30) {
            return 5;
        }
        if (remainingDays <= 180) {
            return 10;
        }
        return 20;
    }

    public static BigDecimal penalty(BigDecimal marketValue, int rate) {
        BigDecimal penalty = Money.round(marketValue.multiply(BigDecimal.valueOf(rate)).movePointLeft(2));
        return penalty.min(MAX_PENALTY);
    }
}
