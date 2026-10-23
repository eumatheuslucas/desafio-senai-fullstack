package br.gov.senai.passe.market;

import br.gov.senai.passe.shared.util.BrasiliaTime;
import java.math.BigDecimal;

public record TransactionView(
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
        String createdAt) {

    public static TransactionView from(MarketTransaction transaction) {
        return new TransactionView(
                transaction.getId(),
                transaction.getClubId(),
                transaction.getPlayerId(),
                transaction.getPlayerName(),
                transaction.getType(),
                transaction.getValue(),
                transaction.getFee(),
                transaction.getPenalty(),
                transaction.getProfit(),
                transaction.getCashChange(),
                BrasiliaTime.format(transaction.getCreatedAt()));
    }
}
