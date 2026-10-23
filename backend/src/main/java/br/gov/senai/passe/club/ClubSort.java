package br.gov.senai.passe.club;

import br.gov.senai.passe.shared.error.BadRequestException;
import br.gov.senai.passe.shared.util.TextNormalizer;
import java.util.Comparator;

public enum ClubSort {
    NAME("name", (a, b) -> 0),
    NET_WORTH_ASC("net_worth_asc", (a, b) -> a.netWorth().compareTo(b.netWorth())),
    NET_WORTH_DESC("net_worth_desc", (a, b) -> b.netWorth().compareTo(a.netWorth()));

    private final String label;
    private final Comparator<ClubView> primary;

    ClubSort(String label, Comparator<ClubView> primary) {
        this.label = label;
        this.primary = primary;
    }

    public static ClubSort parse(String text) {
        for (ClubSort candidate : values()) {
            if (candidate.label.equals(text)) {
                return candidate;
            }
        }
        throw new BadRequestException("Valor inválido para o parâmetro 'sort'.");
    }

    public Comparator<ClubView> comparator() {
        return primary
                .thenComparing((a, b) -> TextNormalizer.fold(a.name()).compareTo(TextNormalizer.fold(b.name())))
                .thenComparing(ClubView::id);
    }
}
