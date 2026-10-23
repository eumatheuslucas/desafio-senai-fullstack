package br.gov.senai.passe.market;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class MarketRulesTest {

    @Test
    void feeIsChargedOnlyAboveThirty() {
        assertEquals(new BigDecimal("0.00"), MarketRules.purchaseFee(new BigDecimal("30.00")));
        assertEquals(new BigDecimal("1.50"), MarketRules.purchaseFee(new BigDecimal("30.01")));
        assertEquals(new BigDecimal("2.38"), MarketRules.purchaseFee(new BigDecimal("47.62")));
    }

    @Test
    void cashLimitIsExactlyHalfOfTheCash() {
        assertTrue(MarketRules.withinCashLimit(new BigDecimal("50.00"), new BigDecimal("100.00")));
        assertFalse(MarketRules.withinCashLimit(new BigDecimal("50.40"), new BigDecimal("100.00")));
        assertFalse(MarketRules.withinCashLimit(new BigDecimal("130.58"), new BigDecimal("261.15")));
    }

    @Test
    void commissionIsProgressiveAndZeroWithoutProfit() {
        assertEquals(new BigDecimal("0.30"), MarketRules.commission(new BigDecimal("3.00")));
        assertEquals(new BigDecimal("0.39"), MarketRules.commission(new BigDecimal("3.85")));
        assertEquals(new BigDecimal("1.00"), MarketRules.commission(new BigDecimal("10.00")));
        assertEquals(new BigDecimal("1.00"), MarketRules.commission(new BigDecimal("10.01")));
        assertEquals(new BigDecimal("2.00"), MarketRules.commission(new BigDecimal("15.00")));
        assertEquals(new BigDecimal("0.00"), MarketRules.commission(new BigDecimal("-7.50")));
    }
}
