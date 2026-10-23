package br.gov.senai.passe.market;

import java.math.BigDecimal;

public record SaleView(
        Integer id,
        Integer clubId,
        Integer playerId,
        String playerName,
        TransactionType type,
        BigDecimal value,
        BigDecimal fee,
        BigDecimal penalty,
        BigDecimal profit,
        BigDecimal cashChange,
        String createdAt,
        BigDecimal purchaseValue,
        int remainingDays,
        int penaltyRate,
        String contractStatus,
        BigDecimal clubCash) {
}
