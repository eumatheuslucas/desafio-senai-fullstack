package br.gov.senai.passe.lineup;

import br.gov.senai.passe.shared.error.BadRequestException;

public enum Formation {
    THREE_ONE("3-1", 3, 0, 1),
    TWO_TWO("2-2", 2, 0, 2),
    TWO_ONE_ONE("2-1-1", 2, 1, 1),
    ONE_TWO_ONE("1-2-1", 1, 2, 1);

    private final String label;
    private final int defenders;
    private final int midfielders;
    private final int attackers;

    Formation(String label, int defenders, int midfielders, int attackers) {
        this.label = label;
        this.defenders = defenders;
        this.midfielders = midfielders;
        this.attackers = attackers;
    }

    public static Formation fromLabel(String text) {
        for (Formation candidate : values()) {
            if (candidate.label.equals(text)) {
                return candidate;
            }
        }
        throw new BadRequestException("Formação inválida. Use 3-1, 2-2, 2-1-1 ou 1-2-1.");
    }

    public String label() {
        return label;
    }

    public int defenders() {
        return defenders;
    }

    public int midfielders() {
        return midfielders;
    }

    public int attackers() {
        return attackers;
    }
}
