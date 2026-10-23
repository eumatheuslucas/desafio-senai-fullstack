package br.gov.senai.passe.lineup;

import br.gov.senai.passe.player.PlayerView;
import java.util.List;

public record LineupView(Integer clubId, String formation, boolean complete, List<PlayerView> players) {

    public static final int STARTERS = 5;
}
