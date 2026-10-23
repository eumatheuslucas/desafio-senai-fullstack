package br.gov.senai.passe.player;

import br.gov.senai.passe.shared.util.TextNormalizer;
import java.util.Comparator;

public final class PlayerOrder {

    public static final Comparator<Player> SQUAD = Comparator
            .comparingInt((Player player) -> player.getPosition().ordinal())
            .thenComparing(player -> TextNormalizer.fold(player.getName()))
            .thenComparing(Player::getId);

    private PlayerOrder() {
    }
}
