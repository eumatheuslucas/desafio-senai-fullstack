package br.gov.senai.passe.market;

import br.gov.senai.passe.shared.error.BadRequestException;
import br.gov.senai.passe.shared.validation.QueryParams;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

public record TransactionFilter(Set<TransactionType> types, Optional<Integer> playerId) {

    public static TransactionFilter parse(QueryParams params) {
        Set<TransactionType> types = EnumSet.noneOf(TransactionType.class);
        params.text("type").ifPresent(raw -> {
            for (String token : raw.split(",", -1)) {
                if (!types.add(parseType(token))) {
                    throw new BadRequestException("O parâmetro 'type' não pode repetir valores.");
                }
            }
        });
        Optional<Integer> playerId = params.integer("player_id", 1, Integer.MAX_VALUE);
        return new TransactionFilter(types, playerId);
    }

    public boolean matches(MarketTransaction transaction) {
        return (types.isEmpty() || types.contains(transaction.getType()))
                && playerId.map(id -> id.equals(transaction.getPlayerId())).orElse(true);
    }

    private static TransactionType parseType(String token) {
        try {
            return TransactionType.valueOf(token);
        } catch (IllegalArgumentException exception) {
            throw new BadRequestException("Valor inválido para o parâmetro 'type'.");
        }
    }
}
