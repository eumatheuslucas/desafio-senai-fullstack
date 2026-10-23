package br.gov.senai.passe.player;

import br.gov.senai.passe.shared.util.TextNormalizer;
import java.util.Comparator;

public enum PlayerSort {
    NAME("name", "name", PlayerSort::byName),
    VALUE_ASC("value_asc", "value", PlayerSort::byValue),
    VALUE_DESC("value_desc", "value", (a, b) -> byValue(b, a)),
    AGE_ASC("age_asc", "age", PlayerSort::byAge),
    AGE_DESC("age_desc", "age", (a, b) -> byAge(b, a)),
    POSITION("position", "position", PlayerSort::byPosition);

    private final String label;
    private final String field;
    private final Comparator<PlayerView> comparator;

    PlayerSort(String label, String field, Comparator<PlayerView> comparator) {
        this.label = label;
        this.field = field;
        this.comparator = comparator;
    }

    public String label() {
        return label;
    }

    public String field() {
        return field;
    }

    public Comparator<PlayerView> comparator() {
        return comparator;
    }

    public static Comparator<PlayerView> tieBreaker() {
        return NAME.comparator.thenComparing(PlayerView::id);
    }

    private static int byName(PlayerView a, PlayerView b) {
        return TextNormalizer.fold(a.name()).compareTo(TextNormalizer.fold(b.name()));
    }

    private static int byValue(PlayerView a, PlayerView b) {
        return a.marketValue().compareTo(b.marketValue());
    }

    private static int byAge(PlayerView a, PlayerView b) {
        return Integer.compare(a.age(), b.age());
    }

    private static int byPosition(PlayerView a, PlayerView b) {
        return Integer.compare(a.position().ordinal(), b.position().ordinal());
    }
}
