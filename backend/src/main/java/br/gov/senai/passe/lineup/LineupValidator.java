package br.gov.senai.passe.lineup;

import br.gov.senai.passe.shared.error.BadRequestException;
import br.gov.senai.passe.shared.validation.JsonBody;
import java.util.HashSet;
import java.util.List;

public final class LineupValidator {

    private LineupValidator() {
    }

    public static LineupRequest parse(JsonBody body) {
        Formation formation = Formation.fromLabel(body.requiredText("formation"));
        List<Integer> playerIds = body.requiredIntArray("player_ids");
        if (playerIds.size() != LineupView.STARTERS) {
            throw new BadRequestException("A escalação deve ter exatamente 5 jogadores.");
        }
        if (new HashSet<>(playerIds).size() != playerIds.size()) {
            throw new BadRequestException("A escalação não pode repetir jogadores.");
        }
        return new LineupRequest(formation, playerIds);
    }
}
