package br.gov.senai.passe.contract;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class ContractTermsTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 11, 5, 16, 40);

    @Test
    void remainingDaysRoundUp() {
        assertEquals(0, ContractTerms.remainingDays(NOW.minusDays(1), NOW));
        assertEquals(0, ContractTerms.remainingDays(NOW, NOW));
        assertEquals(11, ContractTerms.remainingDays(NOW.plusDays(10).plusHours(2), NOW));
        assertEquals(30, ContractTerms.remainingDays(NOW.plusDays(30), NOW));
        assertEquals(31, ContractTerms.remainingDays(NOW.plusDays(30).plusMinutes(1), NOW));
        assertEquals(117, ContractTerms.remainingDays(LocalDateTime.of(2027, 3, 2, 10, 15), NOW));
    }

    @Test
    void penaltyRateFollowsBands() {
        assertEquals(0, ContractTerms.penaltyRate(0));
        assertEquals(5, ContractTerms.penaltyRate(30));
        assertEquals(10, ContractTerms.penaltyRate(31));
        assertEquals(10, ContractTerms.penaltyRate(180));
        assertEquals(20, ContractTerms.penaltyRate(181));
    }

    @Test
    void penaltyIsCappedAtTen() {
        assertEquals(new BigDecimal("4.00"), ContractTerms.penalty(new BigDecimal("40.00"), 10));
        assertEquals(new BigDecimal("8.50"), ContractTerms.penalty(new BigDecimal("42.50"), 20));
        assertEquals(new BigDecimal("10.00"), ContractTerms.penalty(new BigDecimal("75.00"), 20));
        assertEquals(new BigDecimal("1.50"), ContractTerms.penalty(new BigDecimal("30.00"), 5));
    }
}
