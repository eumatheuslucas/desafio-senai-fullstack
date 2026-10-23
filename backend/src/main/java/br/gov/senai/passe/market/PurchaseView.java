package br.gov.senai.passe.market;

import java.math.BigDecimal;

public record PurchaseView(
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
        ContractSummary contract,
        BigDecimal clubCash) {

    public record ContractSummary(BigDecimal purchaseValue, String loyaltyUntil, String status) {
    }
}
