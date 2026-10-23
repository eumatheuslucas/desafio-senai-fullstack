package br.gov.senai.passe.player;

import br.gov.senai.passe.shared.util.TextNormalizer;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public record PlayerFilter(
        Optional<String> search,
        Optional<Position> position,
        Optional<Condition> condition,
        Optional<Boolean> free,
        Optional<Integer> clubId,
        int minAge,
        int maxAge,
        BigDecimal minValue,
        BigDecimal maxValue,
        List<PlayerSort> sort,
        int page,
        int limit) {

    public boolean matches(PlayerView player) {
        return search.map(text -> TextNormalizer.fold(player.name()).contains(text)).orElse(true)
                && position.map(value -> value == player.position()).orElse(true)
                && condition.map(value -> value == player.condition()).orElse(true)
                && free.map(value -> value == (player.clubId() == null)).orElse(true)
                && clubId.map(value -> value.equals(player.clubId())).orElse(true)
                && player.age() >= minAge
                && player.age() <= maxAge
                && player.marketValue().compareTo(minValue) >= 0
                && player.marketValue().compareTo(maxValue) <= 0;
    }

    public Comparator<PlayerView> comparator() {
        Comparator<PlayerView> chain = sort.get(0).comparator();
        for (PlayerSort next : sort.subList(1, sort.size())) {
            chain = chain.thenComparing(next.comparator());
        }
        return chain.thenComparing(PlayerSort.tieBreaker());
    }
}
