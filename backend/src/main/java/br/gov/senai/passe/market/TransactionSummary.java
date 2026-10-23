package br.gov.senai.passe.market;

import br.gov.senai.passe.shared.util.Money;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

public record TransactionSummary(
        BigDecimal totalSpent,
        BigDecimal totalReceived,
        BigDecimal totalFees,
        BigDecimal totalPenalties,
        BigDecimal totalProfit) {

    public static TransactionSummary of(List<MarketTransaction> transactions) {
        return new TransactionSummary(
                sum(transactions, TransactionType.COMPRA, t -> t.getValue().add(t.getFee())),
                sum(transactions, TransactionType.VENDA, MarketTransaction::getCashChange),
                transactions.stream().map(MarketTransaction::getFee).reduce(Money.ZERO, BigDecimal::add),
                sum(transactions, TransactionType.VENDA, MarketTransaction::getPenalty),
                sum(transactions, TransactionType.VENDA, MarketTransaction::getProfit));
    }

    private static BigDecimal sum(List<MarketTransaction> transactions, TransactionType type,
                                  Function<MarketTransaction, BigDecimal> amount) {
        return transactions.stream()
                .filter(transaction -> transaction.getType() == type)
                .map(amount)
                .reduce(Money.ZERO, BigDecimal::add);
    }
}
